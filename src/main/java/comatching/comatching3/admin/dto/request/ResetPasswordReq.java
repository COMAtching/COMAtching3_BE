package comatching.comatching3.admin.dto.request;

import lombok.Data;
import lombok.Getter;

@Data
public class ResetPasswordReq {
    private String token;
    private String password;
    private String confirmPassword;
}
