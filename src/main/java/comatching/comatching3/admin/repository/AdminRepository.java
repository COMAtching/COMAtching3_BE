package comatching.comatching3.admin.repository;

import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByAccountId(String accountId);

    Boolean existsAdminByAccountId(String accountId);

    Optional<Admin> findByUuid(byte[] uuid);

    boolean existsBySchoolEmail(String schoolEmail);
    Optional<Admin> findBySchoolEmail(String schoolEmail);

    List<Admin> findAllAdminsByUniversityAndAccessFalseOrderByCreatedAtDesc(University university);

    Boolean existsAdminByUniversity(University university);

    Boolean existsByUuid(byte[] uuid);
}
