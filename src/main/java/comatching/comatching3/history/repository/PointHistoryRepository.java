package comatching.comatching3.history.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import comatching.comatching3.history.entity.PointHistory;
import comatching.comatching3.users.entity.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
	Optional<List<PointHistory>> findPointHistoriesByUsers(Users users);

	@Query("SELECT ph FROM PointHistory ph JOIN ph.users u WHERE u.username = :username")
	List<PointHistory> findAllByUsername(@Param("username") String username);

	@EntityGraph(attributePaths = {"users", "users.userAiFeature"})
	@Query("SELECT ph FROM PointHistory ph WHERE ph.users.userAiFeature.uuid = :uuid")
	List<PointHistory> findAllByUuid(@Param("uuid") byte[] uuid);

}
