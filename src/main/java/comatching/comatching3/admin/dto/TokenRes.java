package comatching.comatching3.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenRes {

    private String accessToken;
    private String refreshToken;
}
