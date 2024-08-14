package comatching.comatching3.admin.dto.request;

import lombok.Getter;

@Getter
public class EmailVerifyReq {
    private String token;
    private String code;
}
