package comatching.comatching3.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import comatching.comatching3.history.entity.MatchingHistory;

public interface MatchingHistoryRepository extends JpaRepository<MatchingHistory, Long> {

}
