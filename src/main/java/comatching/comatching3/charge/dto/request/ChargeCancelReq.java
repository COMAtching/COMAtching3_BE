package comatching.comatching3.charge.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChargeCancelReq {
    private String userId;
    private LocalDateTime approvalTime;
}
