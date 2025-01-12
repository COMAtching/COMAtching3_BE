package comatching.comatching3.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdminInfoRes {
    private String accountId;
    private String nickname;
    private String university;
    private String role;
    private String schoolEmail;
    private Boolean universityAuth;
}
