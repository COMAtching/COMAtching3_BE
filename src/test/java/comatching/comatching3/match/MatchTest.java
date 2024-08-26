package comatching.comatching3.match;

import comatching.comatching3.history.entity.MatchingHistory;
import comatching.comatching3.history.repository.MatchingHistoryRepository;
import comatching.comatching3.match.dto.messageQueue.MatchResponseMsg;
import comatching.comatching3.match.dto.request.MatchReq;
import comatching.comatching3.match.dto.response.MatchRes;
import comatching.comatching3.match.service.MatchService;
import comatching.comatching3.users.enums.Hobby;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.RabbitMQ.RabbitMQUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.Role;
import comatching.comatching3.util.RabbitMQ.MatchRabbitMQUtil;
import comatching.comatching3.util.UUIDUtil;
import static org.assertj.core.api.Assertions.*;

import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
public class MatchTest {
	@Mock
	private MatchRabbitMQUtil matchRabbitMQUtil;

	@InjectMocks
	private MatchService matchService;

	final byte[] enemyUuid = UUIDUtil.createUUID();
	final byte[] applierUuid = UUIDUtil.createUUID();


	@Autowired
	private UsersRepository usersRepository;

    @Autowired
    private MatchingHistoryRepository matchingHistoryRepository;

	/**
	 * 매칭 신청자 & 결과로 나온 상대 세팅
	 */
	@BeforeEach
	public void beforeEach(){
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
	@DisplayName("매칭 서비스 성공")
	void successMatchLogic(){

		//given
		MatchReq testMatchReq = MatchReq.builder()
				.uuid(UUIDUtil.bytesToHex(applierUuid))
				.ageOption(AgeOption.EQUAL)
				.mbti("EN")
				.hobbyOption(List.of(Hobby.게임, Hobby.독서))
				.contactFrequencyOption(ContactFrequencyOption.FREQUENT)
				.sameMajorOption(false)
				.build();

		MatchResponseMsg matchResponseMsg = new MatchResponseMsg();
		matchResponseMsg.setUuid(UUIDUtil.bytesToHex(enemyUuid));
		Users enemy = usersRepository.findUsersByUuid(enemyUuid).get();
		Users applier = usersRepository.findUsersByUuid(applierUuid).get();

		Integer originalPoint = applier.getPoint();
		Integer originalPickMe = applier.getPickMe();

		when(matchRabbitMQUtil.match(testMatchReq, UUID.randomUUID().toString())).thenReturn(matchResponseMsg);


		//when
		assertThat(matchService.requestMatch(testMatchReq))
				.isInstanceOf(MatchReq.class)
				.isEqualTo(MatchRes.fromUsers(enemy));


		//then
		applier = usersRepository.findUsersByUuid(applierUuid).get();
		MatchingHistory matchingHistory = matchingHistoryRepository.findMatchingHistoriesByApplierId(applier.getId()).get(0);

		assertThat(applier.getPoint()).isEqualTo(originalPoint-800);
		assertThat(applier.getPickMe()).isEqualTo(originalPickMe-1);
		assertThat(matchingHistory).isNotNull();
		assertThat(matchingHistory.getApplier()).isEqualTo(applier);
		assertThat(matchingHistory.getEnemy()).isEqualTo(enemy);
	}
}
