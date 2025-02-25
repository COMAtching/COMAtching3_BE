package comatching.comatching3.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class EmailVerifyReq {
    @NotNull @NotBlank
    private String token;

    @NotNull @NotBlank
    private String code;
}
