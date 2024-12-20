package comatching.comatching3.users.auth.oauth2.provider;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import comatching.comatching3.users.auth.oauth2.dto.UserDto;

public class CustomUser implements UserDetails, OAuth2User {

	private final UserDto userDto;

	public CustomUser(UserDto userDto) {
		this.userDto = userDto;
	}

	public String getUuid() {
		return userDto.getUuid();
	}

	public String getRole() {
		return userDto.getRole();
	}

	@Override
	public String getName() {
		return userDto.getUuid();
	}

	@Override
	public Map<String, Object> getAttributes() {
		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(userDto.getRole()));
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return userDto.getUuid();
	}
}
