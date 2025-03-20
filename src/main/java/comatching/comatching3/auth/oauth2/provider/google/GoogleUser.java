package comatching.comatching3.auth.oauth2.provider.google;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import comatching.comatching3.auth.oauth2.provider.OAuth2ProviderUser;

public class GoogleUser extends OAuth2ProviderUser {

	public GoogleUser(OAuth2User oAuth2User, ClientRegistration clientRegistration){
		super(oAuth2User.getAttributes(), oAuth2User, clientRegistration);
	}

	@Override
	public String getSocialId() {
		return "" + getAttributes().get("sub");
	}

	@Override
	public String getEmail() {
		return (String)getAttributes().get("email");
	}
}
