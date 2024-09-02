package comatching.comatching3.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AfterVerifyEmailRes {
    private String accessToken;
    private String refreshToken;
    private Boolean success;
}
