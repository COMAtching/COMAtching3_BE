package comatching.comatching3.users.auth.oauth2.provider.kakao;

import comatching.comatching3.users.auth.oauth2.provider.OAuth2ProviderUser;
import lombok.Data;

import java.util.Map;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class KakaoUser extends OAuth2ProviderUser {

    private Map<String, Object> kakaoAccount;
    private Map<String, Object> kakaoProfile;

    public KakaoUser(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super(oAuth2User.getAttributes(), oAuth2User, clientRegistration);
        this.kakaoAccount = (Map<String, Object>)getAttributes().get("kakao_account");
        this.kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");
    }


    @Override
    public String getSocialId() {
        return "" + getAttributes().get("id");
    }

    @Override
    public String getUsername() {
        return kakaoProfile.get("nickname").toString();
    }

    @Override
    public String getEmail() {
        return (String) kakaoAccount.get("email");
    }
}
