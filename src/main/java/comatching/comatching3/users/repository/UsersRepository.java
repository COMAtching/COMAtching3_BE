package comatching.comatching3.users.repository;

import comatching.comatching3.users.entity.Users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findBySocialId(String socialId);

    @Query("SELECT u FROM Users u JOIN u.userAiFeature uf WHERE uf.uuid = :uuid")
    Optional<Users> findUsersByUuid(@Param("uuid") byte[] uuid);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END " +
        "FROM Users u " +
        "JOIN u.userAiFeature uf " +
        "WHERE uf.uuid = :uuid")
    boolean existsByUserUuid(@Param("uuid") byte[] uuid);

    @Query("SELECT COUNT(u) FROM Users u WHERE u.username = :username")
    long countUserByUsername(@Param("username") String username);

    @Query("SELECT u FROM Users u " +
        "LEFT JOIN FETCH u.userAiFeature f " +
        "WHERE (:keyword IS NULL OR u.username LIKE %:keyword% OR u.email LIKE %:keyword% OR u.provider LIKE %:keyword%)")
    Page<Users> searchUsersByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
