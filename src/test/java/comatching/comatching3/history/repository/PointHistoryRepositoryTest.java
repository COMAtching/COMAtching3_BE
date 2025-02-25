package comatching.comatching3.history.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import comatching.comatching3.history.entity.PointHistory;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PointHistoryRepositoryTest {

	@Autowired
	private PointHistoryRepository pointHistoryRepository;

	@Autowired
	private UsersRepository usersRepository;

	@Test
	void findAllByUuid() {
		Users user = usersRepository.findById(1L).orElseThrow();

		List<PointHistory> result = pointHistoryRepository.findAllByUuid(user.getUserAiFeature().getUuid());

		assertThat(result).isNotNull();
	}
}