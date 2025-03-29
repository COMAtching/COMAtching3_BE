package comatching.comatching3.matching.service;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.enums.UserCrudType;
import comatching.comatching3.users.repository.UserAiFeatureRepository;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.RabbitMQ.UserCrudRabbitMQUtil;
import comatching.comatching3.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {

    private final UserCrudRabbitMQUtil userCrudRabbitMQUtil;
    private final UsersRepository usersRepository;
    private final UserAiFeatureRepository userAiFeatureRepository;

    public void requestTestCrudCreate() {
        UserAiFeature userAiFeature = userAiFeatureRepository.findById(1L)
                .orElseThrow(() -> new BusinessException(ResponseCode.MATCH_CODE_CHECK_FAIL));
        userCrudRabbitMQUtil.sendUserChange(userAiFeature, UserCrudType.CREATE);
    }


    public void requestTestCrudDelete() {
        UserAiFeature userAiFeature = userAiFeatureRepository.findById(1L)
                .orElseThrow(() -> new BusinessException(ResponseCode.MATCH_CODE_CHECK_FAIL));
        userCrudRabbitMQUtil.sendUserChange(userAiFeature, UserCrudType.DELETE);
    }


    public void requestTestCrudModify() {
        UserAiFeature userAiFeature = userAiFeatureRepository.findById(1L)
                .orElseThrow(() -> new BusinessException(ResponseCode.MATCH_CODE_CHECK_FAIL));
        userCrudRabbitMQUtil.sendUserChange(userAiFeature, UserCrudType.UPDATE);
    }
}
