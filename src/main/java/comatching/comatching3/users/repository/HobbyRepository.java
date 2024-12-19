package comatching.comatching3.users.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import comatching.comatching3.users.entity.Hobby;
import comatching.comatching3.users.entity.UserAiFeature;

@Repository
public interface HobbyRepository extends JpaRepository<Hobby, Long> {

	List<Hobby> findAllByUserAiFeature(UserAiFeature userAiFeature);
}
