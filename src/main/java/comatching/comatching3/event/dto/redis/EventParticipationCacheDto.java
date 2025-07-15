package comatching.comatching3.event.dto.redis;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventParticipationCacheDto implements Serializable {
	private Long id;
	private Long eventId;
	private Long userId;
	private boolean participated;
}


