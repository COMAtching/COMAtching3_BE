package comatching.comatching3.event.repository;

import comatching.comatching3.event.entity.EventParticipation;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> {

    @Query("SELECT ep FROM EventParticipation ep WHERE ep.event.id = :eventId AND ep.user.id = :userId")
    Optional<EventParticipation> findEventParticipationByEventIdAndUserId(@Param("eventId") Long eventId, @Param("userId") Long userId);
}