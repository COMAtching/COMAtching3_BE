package comatching.comatching3.admin.repository;

import comatching.comatching3.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByAccountId(String accountId);
}
