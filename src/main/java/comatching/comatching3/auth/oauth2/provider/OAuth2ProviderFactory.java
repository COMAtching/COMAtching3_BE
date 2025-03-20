package comatching.comatching3.auth.oauth2.provider;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import comatching.comatching3.auth.oauth2.provider.apple.AppleUser;
import comatching.comatching3.auth.oauth2.provider.google.GoogleUser;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.auth.oauth2.provider.kakao.KakaoUser;
import comatching.comatching3.auth.oauth2.provider.naver.NaverUser;
import comatching.comatching3.util.ResponseCode;

public class OAuth2ProviderFactory {
	public static OAuth2ProviderUser getOAuth2UserInfo(ClientRegistration clientRegistration, OAuth2User oAuth2User) {

		String registrationId = clientRegistration.getRegistrationId();

		if (registrationId.equals(OAuth2Provider.KAKAO.getRegistrationId())) {
			return new KakaoUser(oAuth2User, clientRegistration);
		} else if (registrationId.equals(OAuth2Provider.NAVER.getRegistrationId())) {
			return new NaverUser(oAuth2User, clientRegistration);
		} else if (registrationId.equals(OAuth2Provider.GOOGLE.getRegistrationId())) {
			return new GoogleUser(oAuth2User, clientRegistration);
		}
		else if (registrationId.equals(OAuth2Provider.APPLE.getRegistrationId())) {
			return new AppleUser(oAuth2User, clientRegistration);
		}
		else {
			throw new BusinessException(ResponseCode.NOT_SUPPORTED_PROVIDER);
		}
	}
}
