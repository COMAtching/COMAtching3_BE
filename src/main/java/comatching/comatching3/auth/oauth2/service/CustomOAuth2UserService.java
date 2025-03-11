package comatching.comatching3.auth.oauth2.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import comatching.comatching3.auth.oauth2.provider.OAuth2ProviderFactory;
import comatching.comatching3.auth.oauth2.provider.OAuth2ProviderUser;
import comatching.comatching3.auth.oauth2.provider.apple.AppleUser;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.auth.dto.LoginDto;
import comatching.comatching3.auth.details.CustomUser;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.Role;
import comatching.comatching3.users.repository.UserAiFeatureRepository;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UsersRepository usersRepository;
	private final UserAiFeatureRepository userAiFeatureRepository;

	private static final String APPLE_REGISTRATION_ID = "apple";

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		if (registrationId.equalsIgnoreCase(APPLE_REGISTRATION_ID)) {
			Object idTokenObj = userRequest.getAdditionalParameters().get("id_token");
			if (idTokenObj == null) {
				throw new OAuth2AuthenticationException("Missing id_token in additional parameters");
			}
			String idToken = idTokenObj.toString();
			Map<String, Object> attributes = decodeJwtTokenPayload(idToken);
			attributes.put("id_token", idToken);

			OAuth2User dummyUser = new DefaultOAuth2User(
				Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
				attributes,
				"sub"
			);

			return processOAuth2User(userRequest, dummyUser);
		} else {
			OAuth2User oAuth2User = super.loadUser(userRequest);
			return processOAuth2User(userRequest, oAuth2User);
		}
	}

	private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
		ClientRegistration clientRegistration = userRequest.getClientRegistration();

		OAuth2ProviderUser oAuth2UserInfo = OAuth2ProviderFactory.getOAuth2UserInfo(clientRegistration, oAuth2User);

		Optional<Users> userOpt = usersRepository.findBySocialId(oAuth2UserInfo.getSocialId());

		Users user = userOpt.orElseGet(() -> register(oAuth2UserInfo));

		if (!StringUtils.hasText(oAuth2UserInfo.getSocialId())) {
			throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
		}

		LoginDto loginDto = LoginDto.builder()
			.uuid(UUIDUtil.bytesToHex(user.getUserAiFeature().getUuid()))
			.role(user.getRole())
			.build();

		return new CustomUser(loginDto);
	}

	private Users register(OAuth2ProviderUser userInfo) {
		Users newUser = Users.builder()
			.username(userInfo.getUsername())
			.socialId(userInfo.getSocialId())
			.provider(userInfo.getProvider())
			.email(userInfo.getEmail())
			.role(Role.SOCIAL.getRoleName())
			.build();

		byte[] uuid = UUIDUtil.createUUID();
		UserAiFeature userAiFeature = UserAiFeature.builder()
			.users(newUser)
			.uuid(uuid)
			.build();

		newUser.updateUserAiFeature(userAiFeature);

		usersRepository.save(newUser);
		userAiFeatureRepository.save(userAiFeature);

		return newUser;
	}

	private Map<String, Object> decodeJwtTokenPayload(String jwtToken) {
		// JWT는 점(.)으로 구분되며, 두 번째 부분(payload)이 사용자 정보를 담고 있음
		String[] parts = jwtToken.split("\\.");
		if (parts.length < 2) {
			throw new IllegalArgumentException("Invalid JWT token format");
		}
		String payload = parts[1];
		// Base64 URL 디코딩
		byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
		String decodedPayload = new String(decodedBytes, StandardCharsets.UTF_8);
		// JSON을 Map으로 변환 (Jackson ObjectMapper 사용)
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(decodedPayload, new TypeReference<Map<String, Object>>() {});
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse JWT payload", e);
		}
	}
}
