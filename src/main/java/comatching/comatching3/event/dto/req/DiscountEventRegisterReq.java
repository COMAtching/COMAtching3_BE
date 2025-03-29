package comatching.comatching3.event.dto.req;

import comatching.comatching3.admin.enums.EventType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DiscountEventRegisterReq {

    private Long eventId;
    private EventType eventType;
    private LocalDateTime start;
    private LocalDateTime end;

    @Min(5)
    @Max(40)
    private Integer discountRate;
}
