package comatching.comatching3.users.repository;

import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.enums.Gender;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAiFeatureRepository extends JpaRepository<UserAiFeature, Long> {
	Optional<UserAiFeature> findByUuid(byte[] uuid);

	@Query("SELECT COUNT(u) FROM UserAiFeature u " +
		"JOIN u.users us " +
		"WHERE u.gender = 'MALE' AND us.university.universityName = :universityName")
	int getManRatio(@Param("universityName") String universityName);

	@Query("SELECT COUNT(u) FROM UserAiFeature u " +
		"JOIN u.users us " +
		"WHERE u.gender = 'FEMALE' AND us.university.universityName = :universityName")
	int getWomanRatio(@Param("universityName") String universityName);

	@Query("SELECT u FROM UserAiFeature u LEFT JOIN FETCH u.hobbyList " +
		"WHERE u.gender <> :gender " +
		"AND u.dontPickMe = false " +
		"AND u.users.id NOT IN (" +
		"   SELECT mh.enemy.id FROM MatchingHistory mh WHERE mh.applier.id = :applierId " +
		"   UNION " +
		"   SELECT mh.applier.id FROM MatchingHistory mh WHERE mh.enemy.id = :applierId" +
		")")
	List<UserAiFeature> findAllByGenderWithHobbiesExcludingPreviousMatches(@Param("gender") Gender gender,
		@Param("applierId") Long applierId);

	@Query("SELECT u FROM UserAiFeature u LEFT JOIN FETCH u.hobbyList " +
		"WHERE u.gender <> :gender " +
		"AND u.major <> :major " +
		"AND u.dontPickMe = false " +
		"AND u.users.id NOT IN (" +
		"   SELECT mh.enemy.id FROM MatchingHistory mh WHERE mh.applier.id = :applierId " +
		"   UNION " +
		"   SELECT mh.applier.id FROM MatchingHistory mh WHERE mh.enemy.id = :applierId" +
		")")
	List<UserAiFeature> findAllByGenderAndMajorNotWithHobbiesExcludingPreviousMatches(@Param("gender") Gender gender,
		@Param("major") String major,
		@Param("applierId") Long applierId);
}
