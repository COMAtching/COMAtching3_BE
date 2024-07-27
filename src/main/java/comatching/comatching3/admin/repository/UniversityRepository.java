package comatching.comatching3.admin.repository;

import comatching.comatching3.admin.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UniversityRepository extends JpaRepository<University, Long> {
    Optional<University> findByUniversityName(String universityName);
}
