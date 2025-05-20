package comatching.comatching3.event.repository;

import comatching.comatching3.admin.entity.University;
import comatching.comatching3.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT COUNT(e) > 0 FROM Event e WHERE e.university = :university " +
            "AND e.end > :startTime AND e.start < :endTime")
    boolean existsOverlappingEvent(@Param("university") University university,
                                   @Param("startTime") LocalDateTime start,
                                   @Param("endTime") LocalDateTime end);

    @Query("SELECT e FROM Event e WHERE e.university = :university")
    List<Event> findEventsByUniversity(@Param("university") University university);


    @Query("SELECT e FROM Event e WHERE e.university = :university AND CURRENT_TIMESTAMP BETWEEN e.start AND e.end")
    List<Event> findOngoingEventsByUniversity(@Param("university") University university);

    @Query("SELECT e FROM Event e WHERE e.university = :university AND CURRENT_TIMESTAMP > e.end ")
    List<Event> findCloseEventsByUniversity(@Param("university") University university);

    @Query("SELECT e FROM Event e WHERE e.university = :university AND CURRENT_TIMESTAMP > e.start ")
    List<Event> findOpenEventsByUniversity(@Param("university") University university);


    @Query("SELECT e FROM Event e " +
            "WHERE e.start <= :now " +
            "AND e.end >= :now " +
            "AND e.isActivate = true " +  // 공백 추가
            "AND e.university = :university")
    Optional<Event> findCurrentlyActiveEventByUniversity(@Param("now") LocalDateTime now,
                                                         @Param("university") University university);


}
