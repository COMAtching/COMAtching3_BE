package comatching.comatching3.auth.oauth2.provider.apple;

import java.util.Map;

import comatching.comatching3.auth.oauth2.provider.OAuth2ProviderUser;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.StringUtils;

public class AppleUser extends OAuth2ProviderUser {

	public AppleUser(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
		super(oAuth2User.getAttributes(), oAuth2User, clientRegistration);
	}

	@Override
	public String getSocialId() {
		Object sub = getAttributes().get("sub");
		return sub != null ? sub.toString() : "";
	}

	@Override
	public String getUsername() {
		Object nameObj = getAttributes().get("name");
		if (nameObj != null && nameObj instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> nameMap = (Map<String, Object>) nameObj;
			String firstName = nameMap.get("firstName") != null ? nameMap.get("firstName").toString() : "";
			String lastName = nameMap.get("lastName") != null ? nameMap.get("lastName").toString() : "";
			String fullName = (firstName + " " + lastName).trim();
			if (StringUtils.hasText(fullName)) {
				return fullName;
			}
		}

		Object email = getAttributes().get("email");
		if (email != null) {
			return email.toString();
		}

		return getSocialId();
	}

	@Override
	public String getEmail() {
		Object email = getAttributes().get("email");
		return email != null ? email.toString() : "";
	}
}
