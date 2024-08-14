package comatching.comatching3.admin.dto.request;

import lombok.Getter;

@Getter
public class AdminRegisterReq {
    private String accountId;
    private String password;
    private String role;
    private String university;
}
