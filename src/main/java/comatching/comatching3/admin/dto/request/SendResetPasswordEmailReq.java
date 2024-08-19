package comatching.comatching3.admin.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class SendResetPasswordEmailReq {

    private String accountId;

    @Email
    private String schoolEmail;
}
