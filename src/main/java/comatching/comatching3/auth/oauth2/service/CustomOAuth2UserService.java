package comatching.comatching3.auth.oauth2.service;

import java.util.Optional;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import comatching.comatching3.auth.oauth2.provider.OAuth2ProviderFactory;
import comatching.comatching3.auth.oauth2.provider.OAuth2ProviderUser;
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

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);

		return processOAuth2User(userRequest, oAuth2User);
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
}
