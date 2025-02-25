package comatching.comatching3.auth.details;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import comatching.comatching3.auth.dto.LoginDto;

public class CustomUser implements UserDetails, OAuth2User {

	private final LoginDto loginDto;

	public CustomUser(LoginDto loginDto) {
		this.loginDto = loginDto;
	}

	public String getUuid() {
		return loginDto.getUuid();
	}

	public String getRole() {
		return loginDto.getRole();
	}

	@Override
	public String getName() {
		return loginDto.getUuid();
	}

	@Override
	public Map<String, Object> getAttributes() {
		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(loginDto.getRole()));
	}

	@Override
	public String getPassword() {
		return loginDto.getPassword();
	}

	@Override
	public String getUsername() {
		return loginDto.getUuid();
	}
}
