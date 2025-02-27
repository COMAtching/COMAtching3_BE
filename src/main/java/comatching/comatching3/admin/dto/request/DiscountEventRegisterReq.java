package comatching.comatching3.admin.dto.request;

import comatching.comatching3.admin.enums.EventType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class DiscountEventRegisterReq {

    private EventType eventType;
    private LocalDateTime start;
    private LocalDateTime end;

    @Size(min=1, max=40)
    private Integer DiscountRate;
}
