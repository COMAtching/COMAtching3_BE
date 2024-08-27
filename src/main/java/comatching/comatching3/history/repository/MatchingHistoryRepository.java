package comatching.comatching3.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import comatching.comatching3.history.entity.MatchingHistory;

import java.util.List;

public interface MatchingHistoryRepository extends JpaRepository<MatchingHistory, Long> {

    List<MatchingHistory> findMatchingHistoriesByApplierId(Long applierId);
}
