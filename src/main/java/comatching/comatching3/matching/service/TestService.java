package comatching.comatching3.matching.service;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.matching.dto.messageQueue.MatchRequestMsg;
import comatching.comatching3.matching.dto.request.MatchReq;
import comatching.comatching3.matching.enums.AgeOption;
import comatching.comatching3.matching.enums.ContactFrequencyOption;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.HobbyEnum;
import comatching.comatching3.users.enums.UserCrudType;
import comatching.comatching3.users.repository.UserAiFeatureRepository;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.RabbitMQ.MatchRabbitMQUtil;
import comatching.comatching3.util.RabbitMQ.UserCrudRabbitMQUtil;
import comatching.comatching3.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private final UserCrudRabbitMQUtil userCrudRabbitMQUtil;
    private final UsersRepository usersRepository;
    private final UserAiFeatureRepository userAiFeatureRepository;
    private final MatchRabbitMQUtil matchRabbitMQUtil;

    public void requestTestCrudCreate() {
        UserAiFeature userAiFeature = userAiFeatureRepository.findById(1L)
                .orElseThrow(() -> new BusinessException(ResponseCode.MATCH_CODE_CHECK_FAIL));
        userCrudRabbitMQUtil.sendUserChange(userAiFeature, UserCrudType.CREATE);
    }


    public void requestTestCrudDelete(Long id) {
        UserAiFeature userAiFeature = userAiFeatureRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.MATCH_CODE_CHECK_FAIL));
        userCrudRabbitMQUtil.sendUserChange(userAiFeature, UserCrudType.DELETE);
    }


    public void requestTestCrudModify() {
        UserAiFeature userAiFeature = userAiFeatureRepository.findById(1L)
                .orElseThrow(() -> new BusinessException(ResponseCode.MATCH_CODE_CHECK_FAIL));
        userCrudRabbitMQUtil.sendUserChange(userAiFeature, UserCrudType.UPDATE);
    }

    public void requestTestMatch() {
        ArrayList<HobbyEnum> hobbyEnums = new ArrayList<>();
        hobbyEnums.add(HobbyEnum.게임);
        MatchReq matchReq = new MatchReq(AgeOption.EQUAL, "EN", hobbyEnums, ContactFrequencyOption.FREQUENT, false, "");

        log.info("start change 4");
        UserAiFeature applierFeature = userAiFeatureRepository.findById(1L)
                .orElseThrow(() -> new BusinessException(ResponseCode.MATCH_CODE_CHECK_FAIL));

        log.info("applierFeature: {}", applierFeature);
        MatchRequestMsg msg = new MatchRequestMsg();
        msg.fromMatchReqAndUserAiFeature(matchReq, applierFeature, "가톨릭대학교");

        matchRabbitMQUtil.match(msg, "123d456d");
    }

    public void initCsv() throws Exception {
        List<Users> allUsers = usersRepository.findAll();

        for (Users user : allUsers) {
            Thread.sleep(100);
            if (user.getUserAiFeature().getContactFrequency() == null || user.getPickedCount() >= 5) continue;
            userCrudRabbitMQUtil.sendUserChange(user.getUserAiFeature(), UserCrudType.CREATE);
        }
    }
}
