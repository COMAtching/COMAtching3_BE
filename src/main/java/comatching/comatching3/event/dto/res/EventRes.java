package comatching.comatching3.event.dto.res;

import comatching.comatching3.admin.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class EventRes {
    protected Long eventId;
    protected LocalDateTime start;
    protected LocalDateTime end;
    protected EventType eventType;
    protected Boolean isActive;
}
