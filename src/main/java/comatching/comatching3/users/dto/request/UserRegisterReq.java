package comatching.comatching3.users.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class UserRegisterReq {

	@Email
	private String accountId;
	private String password;
}
