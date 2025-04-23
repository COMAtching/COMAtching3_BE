package comatching.comatching3.event.repository;

import comatching.comatching3.admin.entity.University;
import comatching.comatching3.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT COUNT(e) > 0 FROM Event e WHERE e.university = :university " +
            "AND e.end > :startTime AND e.start < :endTime")
    boolean existsOverlappingEvent(@Param("university") University university,
                                   @Param("startTime") LocalDateTime start,
                                   @Param("endTime") LocalDateTime end);

    @Query("SELECT e FROM Event e WHERE e.university = :university")
    List<Event> findEventsByUniversity(@Param("university") University university);

//    @Query("SELECT e FROM  Event e WHERE e.university = :university AND Now() >= :startTime AND Now() <= :endTime")
//    Event findEventByUniversityBetween(@Param("university") University university,
//                                       @Param("startTime") LocalDateTime start,
//                                       @Param("endTime") LocalDateTime end);

    @Query("SELECT e FROM Event e WHERE e.university = :university AND CURRENT_TIMESTAMP BETWEEN e.start AND e.end")
    List<Event> findOngoingEventsByUniversity(@Param("university") University university);

}
