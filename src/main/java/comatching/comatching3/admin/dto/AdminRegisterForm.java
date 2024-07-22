package comatching.comatching3.admin.dto;

import lombok.Getter;

@Getter
public class AdminRegisterForm {
    private String accountId;
    private String password;
    private String universityName;
    private Boolean isUniversityVerified;
    private String role;
}
