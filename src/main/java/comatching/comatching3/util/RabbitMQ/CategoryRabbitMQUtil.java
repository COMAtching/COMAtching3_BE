package comatching.comatching3.util.RabbitMQ;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.matching.dto.messageQueue.MatchResponseMsg;
import comatching.comatching3.users.dto.messageQueue.CategoryReqMsg;
import comatching.comatching3.util.ResponseCode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryRabbitMQUtil {
    private final RabbitTemplate rabbitTemplate;

    private String categoryQueue;

    /**
     * 소분류 대분류로 분류 요청하는 메서드
     * @param requestMsg
     * @return
     */
    public List<String> classifyCategory(CategoryReqMsg requestMsg){
        String requestId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(requestId);
        ParameterizedTypeReference<MatchResponseMsg> responseType = new ParameterizedTypeReference<MatchResponseMsg>(){};

        MatchResponseMsg response = rabbitTemplate.convertSendAndReceiveAsType(
                categoryQueue,
                requestMsg,
                (MessagePostProcessor) null,
                correlationData,
                responseType);

        if(response == null){
            throw new BusinessException(ResponseCode.NO_MATCH_RESPONSE);
        }
        List<String> result = new ArrayList<>();

        return result;
    }
}
