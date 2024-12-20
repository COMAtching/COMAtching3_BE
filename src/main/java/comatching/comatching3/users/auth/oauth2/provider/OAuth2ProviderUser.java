package comatching.comatching3.users.auth.oauth2.provider;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Data;

@Data
public abstract class OAuth2ProviderUser implements ProviderUser{

	private Map<String, Object> attributes;
	private OAuth2User oAuth2User;
	private ClientRegistration clientRegistration;

	public OAuth2ProviderUser(Map<String, Object> attributes, OAuth2User oAuth2User,
		ClientRegistration clientRegistration) {
		this.attributes = attributes;
		this.oAuth2User = oAuth2User;
		this.clientRegistration = clientRegistration;
	}

	@Override
	public String getProvider() {
		return clientRegistration.getRegistrationId();
	}

	@Override
	public List<? extends GrantedAuthority> getAuthorities() {
		return oAuth2User.getAuthorities().stream().map(authority ->
			new SimpleGrantedAuthority(authority.getAuthority())).collect(Collectors.toList());
	}
}
