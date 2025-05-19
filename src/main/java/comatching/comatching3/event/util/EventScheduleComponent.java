package comatching.comatching3.event.util;

import comatching.comatching3.event.entity.Event;
import comatching.comatching3.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventScheduleComponent {

    private final EventRepository eventRepository;

    /**
     * 10분 마다 등록된 event의 활성화/비활성화 여부 판단
     */
    @Scheduled(cron = "0 0/10 * * * *")
    public void activeEvent() {
        List<Event> events = eventRepository.findAll();

        if (events == null || events.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        for (Event event : events) {
            if (event.getStart().truncatedTo(ChronoUnit.MINUTES).equals(now)) {
                event.setIsActivate(true);
            }

            if (event.getEnd().truncatedTo(ChronoUnit.MINUTES).equals(now)) {
                event.setIsActivate(false);
            }
        }
    }
}
