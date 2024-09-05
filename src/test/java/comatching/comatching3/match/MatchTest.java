package comatching.comatching3.match;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import comatching.comatching3.history.entity.MatchingHistory;
import comatching.comatching3.history.repository.MatchingHistoryRepository;
import comatching.comatching3.match.dto.messageQueue.MatchResponseMsg;
import comatching.comatching3.match.dto.request.MatchReq;
import comatching.comatching3.match.dto.response.MatchRes;
import comatching.comatching3.match.enums.AgeOption;
import comatching.comatching3.match.enums.ContactFrequencyOption;
import comatching.comatching3.match.service.MatchService;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.Hobby;
import comatching.comatching3.users.enums.Role;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.RabbitMQ.MatchRabbitMQUtil;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MatchTest {
	@Mock
	private MatchRabbitMQUtil matchRabbitMQUtil;

	@Mock
	private UsersRepository usersRepository;

	@Mock
	private SecurityUtil securityUtil;

	@InjectMocks
	private MatchService matchService;

	private byte[] enemyUuid;
	private byte[] applierUuid;
	private UserAiFeature applierFeature;
	private UserAiFeature enemyFeature;
	private Users applier;
	private Users enemy;

	@Autowired
	private MatchingHistoryRepository matchingHistoryRepository;

	@Autowired
	private EntityManager em;

	/**
	 * 매칭 신청자 & 결과로 나온 상대 세팅
	 */
	@BeforeEach
	void beforeEach(){
		enemyUuid = UUIDUtil.createUUID();
		applierUuid = UUIDUtil.createUUID();

		enemyFeature = UserAiFeature.builder()
			.uuid(enemyUuid)
			.build();

		applierFeature = UserAiFeature.builder()
			.uuid(applierUuid)
			.build();

		em.persist(enemyFeature);
		em.persist(applierFeature);

		applier = Users.builder()
			.role(Role.USER.getRoleName())
			.build();

		enemy = Users.builder()
			.role(Role.USER.getRoleName())
			.build();

		applier.updateUserAiFeature(applierFeature);
		enemy.updateUserAiFeature(enemyFeature);

		//System.out.println(UUIDUtil.bytesToHex(enemy.getUserAiFeature().getUuid()));
		em.persist(applier);
		em.persist(enemy);

	}

	@Test
	@DisplayName("매칭 서비스 성공")
	void successMatchLogic(){

		/*usersRepository.save(applier);
		usersRepository.save(enemy);*/

		//given
		MatchReq testMatchReq = MatchReq.builder()
				.uuid(UUIDUtil.bytesToHex(applierUuid))
				.ageOption(AgeOption.EQUAL)
				.mbtiOption("EN")
				.hobbyOption(List.of(Hobby.게임, Hobby.독서))
				.contactFrequencyOption(ContactFrequencyOption.FREQUENT)
				.sameMajorOption(false)
				.build();

		MatchResponseMsg matchResponseMsg = new MatchResponseMsg();
		matchResponseMsg.setEnemyUuid(UUIDUtil.bytesToHex(enemyUuid));
		System.out.println("uuid: " + matchResponseMsg.getEnemyUuid());
		Users enemy = em.find(Users.class, this.enemy.getId());
		Users applier = em.find(Users.class, this.applier.getId());

		Integer originalPoint = applier.getPoint();
		Integer originalPickMe = applier.getPickMe();

		when(matchRabbitMQUtil.match(any(MatchReq.class), anyString())).thenReturn(matchResponseMsg);
		when(usersRepository.findUsersByUuid(enemyUuid)).thenReturn(Optional.of(enemy));
		when(securityUtil.getCurrentUsersEntity()).thenReturn(applier);


		//when
		assertThat(matchService.requestMatch(testMatchReq))
				.isInstanceOf(MatchReq.class)
				.isEqualTo(MatchRes.fromUsers(enemy));


		//then
		applier = em.find(Users.class, this.applier.getId());
		MatchingHistory matchingHistory = matchingHistoryRepository.findMatchingHistoriesByApplierId(applier.getId()).get().get(0);

		assertThat(applier.getPoint()).isEqualTo(originalPoint-800);
		assertThat(applier.getPickMe()).isEqualTo(originalPickMe-1);
		assertThat(matchingHistory).isNotNull();
		assertThat(matchingHistory.getApplier()).isEqualTo(applier);
		assertThat(matchingHistory.getEnemy()).isEqualTo(enemy);
	}
}
