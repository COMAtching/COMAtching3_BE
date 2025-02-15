package comatching.comatching3.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordReq {
	@NotNull
	@NotBlank
	private String token;

	@NotNull
	@NotBlank
	private String password;

	@NotNull
	@NotBlank
	private String confirmPassword;
}
