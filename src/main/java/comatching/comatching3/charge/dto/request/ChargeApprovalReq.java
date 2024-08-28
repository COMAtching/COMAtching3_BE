package comatching.comatching3.charge.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChargeApprovalReq {
    private String userId;
    private Integer amount;
    private LocalDateTime approvalTime;
}
