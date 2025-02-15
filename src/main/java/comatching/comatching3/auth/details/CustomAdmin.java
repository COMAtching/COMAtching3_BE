package comatching.comatching3.auth.details;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import comatching.comatching3.auth.dto.LoginDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomAdmin implements UserDetails {

	private final LoginDto adminDto;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(adminDto.getRole()));
	}

	@Override
	public String getPassword() {
		return adminDto.getPassword();
	}

	@Override
	public String getUsername() {
		return adminDto.getAccountId();
	}

	public String getUuid() {
		return adminDto.getUuid();
	}

	public String getRole() {
		return adminDto.getRole();
	}
}
