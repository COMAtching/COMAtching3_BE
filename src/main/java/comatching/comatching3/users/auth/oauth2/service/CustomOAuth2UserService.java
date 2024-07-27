package comatching.comatching3.users.auth.oauth2.service;

import comatching.comatching3.users.auth.oauth2.dto.CustomOAuth2User;
import comatching.comatching3.users.auth.oauth2.dto.KakaoResponse;
import comatching.comatching3.users.auth.oauth2.dto.KakaoUserDto;
import comatching.comatching3.users.auth.oauth2.dto.OAuth2Response;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.Role;
import comatching.comatching3.users.repository.UsersRepository;
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
        Optional<Users> user = usersRepository.findBySocialId(socialId);
        KakaoUserDto kakaoUserDto;

        if (user.isEmpty()) {
            Users newUser = Users.builder()
                    .socialId(socialId)
                    .role(Role.SOCIAL.getRoleName())
                    .build();

            usersRepository.save(newUser);

            kakaoUserDto = KakaoUserDto.builder()
                    .socialId(socialId)
                    .role(newUser.getRole())
                    .build();
        } else {
            kakaoUserDto = KakaoUserDto.builder()
                    .socialId(socialId)
                    .role(user.get().getRole())
                    .build();
        }

        return new CustomOAuth2User(kakaoUserDto);
    }
}
