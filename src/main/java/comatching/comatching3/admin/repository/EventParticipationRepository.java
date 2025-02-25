package comatching.comatching3.admin.repository;

import comatching.comatching3.admin.entity.event.EventParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> {

}
