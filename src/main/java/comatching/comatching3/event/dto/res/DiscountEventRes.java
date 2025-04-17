package comatching.comatching3.event.dto.res;

import comatching.comatching3.admin.enums.EventType;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class DiscountEventRes extends EventRes {
    Integer discountRate;

    public DiscountEventRes(Long eventId, LocalDateTime start, LocalDateTime end,
                            EventType eventType, Boolean isActive, Integer discountRate) {
        super(eventId, start, end, eventType, isActive);
        this.discountRate = discountRate;
    }

    public DiscountEventRes() {
        super();
    }
}
