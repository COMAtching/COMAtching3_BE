package comatching.comatching3.users.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import comatching.comatching3.admin.entity.University;
import comatching.comatching3.users.entity.Users;

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

	Optional<Users> findByUsernameAndUniversity(String username, University university);

	Optional<Users> findByEmailAndUniversity(String Email, University university);

	Page<Users> findALlByUniversityOrderByCreatedAtAsc(Pageable pageable, University university);
}
