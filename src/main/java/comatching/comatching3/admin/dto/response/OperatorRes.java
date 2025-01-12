package comatching.comatching3.admin.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OperatorRes {
    private String uuid;
    private String accountId;
    private String nickname;
    private String email;
    private LocalDateTime requestAt;
}
