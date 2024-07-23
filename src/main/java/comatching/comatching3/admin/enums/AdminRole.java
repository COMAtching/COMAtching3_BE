package comatching.comatching3.admin.enums;

import lombok.Getter;

@Getter
public enum AdminRole {
	ROLE_ADMIN("ROLE_ADMIN"),
	ROLE_OPERATOR("ROLE_OPERATOR");

	private final String roleName;

	AdminRole(String role) {
		this.roleName = role;
	}

}
