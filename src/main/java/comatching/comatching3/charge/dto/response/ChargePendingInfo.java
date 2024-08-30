package comatching.comatching3.charge.dto.response;

import comatching.comatching3.util.UUIDUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChargePendingInfo {
    private String userId;
    private String username;
    private Integer requestAmount;
    private Integer existingPoints;
    private LocalDateTime createdAt;

    @Builder
    public ChargePendingInfo(byte[] userId, String username, Integer requestAmount, Integer existingPoints, LocalDateTime createdAt) {
        this.userId = UUIDUtil.bytesToHex(userId);
        this.username = username;
        this.requestAmount = requestAmount;
        this.existingPoints = existingPoints;
        this.createdAt = createdAt;
    }
}
