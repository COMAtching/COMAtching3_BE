package comatching.comatching3.admin.dto.request;

import comatching.comatching3.admin.enums.EventType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class DiscountEventRegisterReq {

    private EventType eventType;
    private LocalDateTime start;
    private LocalDateTime end;

    @Min(5)
    @Max(40)
    private Integer discountRate;
}
