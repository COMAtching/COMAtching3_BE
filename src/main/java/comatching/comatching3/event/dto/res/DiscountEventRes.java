package comatching.comatching3.event.dto.res;

import comatching.comatching3.admin.enums.EventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@NoArgsConstructor
public class DiscountEventRes {
    private Long eventId;
    private LocalDateTime start;
    private LocalDateTime end;
    private EventType eventType;
    Integer discountRate;

}
