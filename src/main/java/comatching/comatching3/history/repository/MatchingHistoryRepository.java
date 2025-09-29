package comatching.comatching3.history.repository;

import comatching.comatching3.history.entity.MatchingHistory;
import comatching.comatching3.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchingHistoryRepository extends JpaRepository<MatchingHistory, Long> {

    @Query("SELECT mh FROM MatchingHistory mh WHERE mh.applier.id = :applierId ORDER BY mh.createdAt DESC")
    Optional<List<MatchingHistory>> findByApplierId(@Param("applierId") Long applierId);

    @Query("SELECT mh FROM MatchingHistory mh WHERE mh.enemy.id = :enemy ORDER BY mh.createdAt DESC")
    Optional<List<MatchingHistory>> findByEnemyId(@Param("enemyId") Long enemyId);

    Optional<List<MatchingHistory>> findByApplier(Users users);

    Long countByApplier(Users users);

    @Query("SELECT COUNT(h) FROM MatchingHistory h WHERE h.applier = :applier AND DATE(h.createdAt) = CURRENT_DATE")
    Long countTodayByApplier(@Param("applier") Users applier);
}
