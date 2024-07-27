package comatching.comatching3.users.enums;

import lombok.Getter;

@Getter
public enum Role {
	SOCIAL("ROLE_SOCIAL"),
	USER("ROLE_USER");

	private final String roleName;

	Role(String role) {
		this.roleName = role;
	}
}
