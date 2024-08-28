package comatching.comatching3.charge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ApprovalRes {
    private String requestId;
    private LocalDateTime timeStamp;
}
