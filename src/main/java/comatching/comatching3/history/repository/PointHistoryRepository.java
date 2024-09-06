package comatching.comatching3.history.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import comatching.comatching3.history.entity.PointHistory;
import comatching.comatching3.users.entity.Users;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
	Optional<List<PointHistory>> findPointHistoriesByUsers(Users users);
}
