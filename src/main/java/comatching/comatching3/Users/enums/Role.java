package comatching.comatching3.Users.enums;

import lombok.Getter;

@Getter
public enum Role {
	SOCIAL("ROLE_SOCIAL"),
	USER("ROLE_USER"),
	ADMIN("ROLE_ADMIN");

	private final String roleName;

	Role(String role) {
		this.roleName = role;
	}
}
