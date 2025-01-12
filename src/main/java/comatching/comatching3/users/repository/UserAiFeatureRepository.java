package comatching.comatching3.users.repository;

import comatching.comatching3.users.entity.UserAiFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
