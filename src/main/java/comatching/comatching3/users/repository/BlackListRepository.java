package comatching.comatching3.users.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import comatching.comatching3.admin.entity.University;
import comatching.comatching3.users.entity.BlackList;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList, Long> {
	void deleteByUuid(byte[] uuid);

	boolean existsByUuid(byte[] uuid);

	boolean existsByEmail(String email);

	Optional<BlackList> findByUuid(byte[] uuid);

	List<BlackList> findAllByUniversityOrderByCreatedAtDesc(String university);
}
