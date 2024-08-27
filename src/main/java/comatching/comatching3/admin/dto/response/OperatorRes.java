package comatching.comatching3.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OperatorRes {
    private String uuid;
    private String nickname;
    private Boolean access;
}
