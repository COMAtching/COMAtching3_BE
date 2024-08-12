package comatching.comatching3.users.auth.oauth2.service;

import comatching.comatching3.users.auth.oauth2.dto.CustomOAuth2User;
import comatching.comatching3.users.auth.oauth2.dto.KakaoResponse;
import comatching.comatching3.users.auth.oauth2.dto.UserDto;
import comatching.comatching3.users.auth.oauth2.dto.OAuth2Response;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.Role;
import comatching.comatching3.users.repository.UserAiFeatureRepository;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.UUIDUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UsersRepository usersRepository;
    private final UserAiFeatureRepository userAiFeatureRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2Response oAuth2Response = null;

        if(userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String socialId = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        String username = oAuth2Response.getNickname();

        Optional<Users> user = usersRepository.findBySocialId(socialId);
        UserDto userDto;


        if (user.isEmpty()) {
            Users newUser = Users.builder()
                    .socialId(socialId)
                    .role(Role.SOCIAL.getRoleName())
                    .username(username)
                    .build();

            byte[] uuid = UUIDUtil.createUUID();
            UserAiFeature userAiFeature = UserAiFeature.builder()
                    .users(newUser)
                    .uuid(uuid)
                    .build();

            newUser.updateUserAiFeature(userAiFeature);

            usersRepository.save(newUser);
            userAiFeatureRepository.save(userAiFeature);

            userDto = UserDto.builder()
                    .uuid(UUIDUtil.bytesToHex(uuid))
                    .role(newUser.getRole())
                    .nickname(username)
                    .build();
        } else {
            Users existUser = user.get();
            byte[] uuid = existUser.getUserAiFeature().getUuid();

            userDto = UserDto.builder()
                    .uuid(UUIDUtil.bytesToHex(uuid))
                    .role(user.get().getRole())
                    .nickname(username)
                    .build();
        }

        return new CustomOAuth2User(userDto);
    }
}
