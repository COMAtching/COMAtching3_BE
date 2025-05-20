package comatching.comatching3.util.RabbitMQ;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.dto.messageQueue.CategoryReqMsg;
import comatching.comatching3.users.dto.messageQueue.CategoryResMsg;
import comatching.comatching3.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryRabbitMQUtil {
    private final RabbitTemplate rabbitTemplate;

    private String categoryQueue = "classifier";

    /**
     * 소분류 대분류로 분류 요청하는 메서드
     *
     * @param requestMsg
     * @return
     */
    public CategoryResMsg classifyCategory(CategoryReqMsg requestMsg) {
        String requestId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(requestId);
        ParameterizedTypeReference<CategoryResMsg> responseType = new ParameterizedTypeReference<CategoryResMsg>() {
        };

        CategoryResMsg response = rabbitTemplate.convertSendAndReceiveAsType(
                categoryQueue,
                requestMsg,
                (MessagePostProcessor) null,
                correlationData,
                responseType);

        if (response == null) {
            throw new BusinessException(ResponseCode.NO_MATCH_RESPONSE);
        }

        log.info("[ClassifyCategory] small={} big={}", requestMsg.getSmallCategory().toString(), response.getBigCategory().toString());
        return response;
    }
}
