package comatching.comatching3.admin.repository;

import comatching.comatching3.admin.entity.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
