package comatching.comatching3.users.repository;

import comatching.comatching3.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findBySocialId(String socialId);

    @Query("SELECT u FROM Users u JOIN u.userAiFeature uf WHERE uf.uuid = :uuid")
    Optional<Users> findUsersByUuid(@Param("uuid") byte[] uuid);
}
