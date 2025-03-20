package comatching.comatching3.auth.oauth2.provider.kakao;

import comatching.comatching3.auth.oauth2.provider.OAuth2ProviderUser;

import java.util.Map;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class KakaoUser extends OAuth2ProviderUser {

    private Map<String, Object> kakaoAccount;

    public KakaoUser(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super(oAuth2User.getAttributes(), oAuth2User, clientRegistration);
        this.kakaoAccount = (Map<String, Object>)getAttributes().get("kakao_account");
    }


    @Override
    public String getSocialId() {
        return "" + getAttributes().get("id");
    }

    @Override
    public String getEmail() {
        return (String) kakaoAccount.get("email");
    }
}
