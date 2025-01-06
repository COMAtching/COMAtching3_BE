package comatching.comatching3.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import comatching.comatching3.users.entity.BlackList;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList, Long> {
	void deleteByUuid(byte[] uuid);

	boolean existsByUuid(byte[] uuid);

	Optional<BlackList> findByUuid(byte[] uuid);
}
