package comatching.comatching3.users;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.Role;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.UUIDUtil;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsersRepositoryTest {

	private byte[] testUuid;

	@Autowired
	private UsersRepository usersRepository;

	@BeforeEach
	public void beforeEach() {
		testUuid = UUIDUtil.createUUID();
	}

	@Test
	void findUsersByUuid() {
		//given
		Users testUser = Users.builder()
			.role(Role.USER.getRoleName())
			.username("TestUser")
			.build();

		UserAiFeature testAiFeature = UserAiFeature.builder()
			.uuid(testUuid)
			.users(testUser)  // 관계 설정 추가
			.build();

		testUser.updateUserAiFeature(testAiFeature);

		usersRepository.save(testUser);

		//when
		Users checkUser = usersRepository.findUsersByUuid(testUuid).orElseThrow();

		//then
		assertThat(checkUser.getUserAiFeature().getUuid()).isEqualTo(testUuid);
		assertThat(checkUser.getId()).isEqualTo(testUser.getId());
	}

	@Test
	void countUserByUsername() {
		String username = "adfadsf";

		Long count = usersRepository.countUserByUsername(username);

		assertThat(count).isEqualTo(0);
	}
}
