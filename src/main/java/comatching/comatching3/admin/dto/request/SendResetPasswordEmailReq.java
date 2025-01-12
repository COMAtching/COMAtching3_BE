package comatching.comatching3.admin.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SendResetPasswordEmailReq {

    @NotNull
    @NotBlank
    private String accountId;

    @Email
    @NotNull @NotBlank
    private String email;
}
