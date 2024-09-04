package comatching.comatching3.history.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import comatching.comatching3.history.entity.MatchingHistory;

public interface MatchingHistoryRepository extends JpaRepository<MatchingHistory, Long> {

    Optional<List<MatchingHistory>> findMatchingHistoriesByApplierId(Long applierId);
}
