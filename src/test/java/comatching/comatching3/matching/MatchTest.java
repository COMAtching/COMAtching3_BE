package comatching.comatching3.matching;

import comatching.comatching3.history.repository.MatchingHistoryRepository;
import comatching.comatching3.matching.service.MatchService;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.Role;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.RabbitMQ.MatchRabbitMQUtil;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
    void beforeEach() {
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
}
