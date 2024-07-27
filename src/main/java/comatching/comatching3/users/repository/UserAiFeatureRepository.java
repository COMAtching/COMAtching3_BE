package comatching.comatching3.users.repository;

import comatching.comatching3.users.entity.UserAiFeature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAiFeatureRepository extends JpaRepository<UserAiFeature, Long> {
    Optional<UserAiFeature> findByUuid(byte[] uuid);
}
