package comatching.comatching3.users.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import comatching.comatching3.users.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

	List<Report> findReportsByProcesssingIsFalse();
}
