package comatching.comatching3.match;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.Role;
import comatching.comatching3.util.RabbitMQ.MatchRabbitMQUtil;
import comatching.comatching3.util.UUIDUtil;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
public class MatchTest {
	@Mock
	private MatchRabbitMQUtil matchRabbitMQUtil;

	static final byte[] enemyUuid = UUIDUtil.createUUID();
	static final byte[] applierUuid = UUIDUtil.createUUID();

	/**
	 * 매칭 신청자 & 결과로 나온 상대 세팅
	 */
	@BeforeAll
	public static void beforeAll(){
		UserAiFeature applierFeature = UserAiFeature.builder()
			.uuid(applierUuid)
			.build();

		UserAiFeature enemyFeature = UserAiFeature.builder()
			.uuid(enemyUuid)
			.build();

		Users applier = Users.builder()
			.role(Role.USER.getRoleName())
			.build();

		Users enemy = Users.builder()
			.role(Role.USER.getRoleName())
			.build();

		applier.updateUserAiFeature(applierFeature);
		enemy.updateUserAiFeature(enemyFeature);

	}

	@Test
	@DisplayName("매칭 로직 확인")
	void successMatchLogic(){
		//given


	}
}
