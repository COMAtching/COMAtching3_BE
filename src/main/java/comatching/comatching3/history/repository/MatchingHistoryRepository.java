package comatching.comatching3.history.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import comatching.comatching3.history.entity.MatchingHistory;
import comatching.comatching3.users.entity.Users;

public interface MatchingHistoryRepository extends JpaRepository<MatchingHistory, Long> {

    @Query("SELECT mh FROM MatchingHistory mh WHERE mh.applier.id = :applierId")
    Optional<List<MatchingHistory>> findByApplierId(@Param("applierId") Long applierId);

    Optional<List<MatchingHistory>> findByApplier(Users users);

}
