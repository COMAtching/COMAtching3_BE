package comatching.comatching3.users.dto;

import lombok.Getter;

@Getter
public class AnonymousUser {

	private String username = "anonymous";

	private String password = "anonymous";

	private String email = "anonymous@anonymous.com";

	private String role = "ROLE_ANONYMOUS";

	private String schoolEmail = "anonymous";

	private String contactId = "anonymous";
}
