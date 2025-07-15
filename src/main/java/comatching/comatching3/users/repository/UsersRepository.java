package comatching.comatching3.users.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import comatching.comatching3.admin.entity.University;
import comatching.comatching3.users.entity.Users;
import jakarta.persistence.LockModeType;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

	Optional<Users> findBySocialId(String socialId);

	Optional<Users> findByEmail(String email);

	boolean existsByEmail(String email);

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

	@Query("SELECT u FROM Users u JOIN u.userAiFeature ua " +
		"WHERE ua.uuid = :uuid " +
		"AND u.university = :university")
	Optional<Users> findUsersByUuidAndUniversity(@Param("uuid") byte[] uuid,
		@Param("university") University university);

	Page<Users> findALlByUniversityOrderByCreatedAtAsc(Pageable pageable, University university);

	boolean existsByContactId(String contactId);

	boolean existsBySchoolEmail(String schoolEmail);

	List<Users> findAllUserByUniversityId(Long universityId);

	Page<Users> findAllByUniversityAndEmailContainingIgnoreCaseOrderByCreatedAtAsc(
		University university, String email, Pageable pageable);

	// 사용자명으로 검색하는 페이징 메서드
	Page<Users> findAllByUniversityAndUsernameContainingIgnoreCaseOrderByCreatedAtAsc(
		University university, String username, Pageable pageable);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT u FROM Users u JOIN u.userAiFeature uf WHERE uf.uuid = :uuid")
	Optional<Users> findUsersByUuidForUpdate(@Param("uuid") byte[] uuid);

}
