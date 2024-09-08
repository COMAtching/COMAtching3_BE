package comatching.comatching3.history.dto.res;

import comatching.comatching3.history.enums.PointHistoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PointHistoryRes {
    private String username;
    private PointHistoryType pointHistoryType;
    private Integer changeAmount;
    private Integer totalPoint;
    private Integer pickMe;
    private LocalDateTime timeStamp;
}
