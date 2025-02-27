package comatching.comatching3.admin.repository;

import comatching.comatching3.admin.entity.University;
import comatching.comatching3.admin.entity.event.Event;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT COUNT(e) > 0 FROM Event e WHERE e.university = :university " +
            "AND e.end > :startTime AND e.start < :endTime")
    boolean existsOverlappingEvent(@Param("university") University university,
                                   @Param("startTime") LocalDateTime start,
                                   @Param("endTime") LocalDateTime end);
}
