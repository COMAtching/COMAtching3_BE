package comatching.comatching3.users.repository;

import comatching.comatching3.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findBySocialId(String socialId);
}
