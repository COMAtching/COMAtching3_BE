package comatching.comatching3.admin.dto.request;

import comatching.comatching3.admin.dto.valid.ValidRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AdminRegisterReq {
	@NotNull
	@NotBlank
	private String accountId;

	@NotNull
	@NotBlank
	private String password;

	@NotNull
	@NotBlank
	private String schoolEmail;

	@NotNull
	@NotBlank
	private String nickname;

	@ValidRole
	@NotNull
	@NotBlank
	private String role;

	@NotNull
	@NotBlank
	private String university;
}
