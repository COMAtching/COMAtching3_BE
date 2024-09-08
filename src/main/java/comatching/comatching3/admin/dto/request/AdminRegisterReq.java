package comatching.comatching3.admin.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRegisterReq {
    private String accountId;
    private String password;
    private String nickname;
    private String role;
    private String university;
}
