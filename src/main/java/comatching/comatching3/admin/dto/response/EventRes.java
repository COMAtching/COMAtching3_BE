package comatching.comatching3.admin.dto.response;

import comatching.comatching3.admin.enums.EventType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter @Getter
@NoArgsConstructor
public class EventRes{
    private LocalDateTime start;
    private LocalDateTime end;
    private EventType eventType;
    Integer discountRate;

}
