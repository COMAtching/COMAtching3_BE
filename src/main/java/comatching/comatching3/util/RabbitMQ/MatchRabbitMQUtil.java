package comatching.comatching3.util.RabbitMQ;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.match.dto.messageQueue.MatchRequestMsg;
import comatching.comatching3.match.dto.messageQueue.MatchResponseMsg;
import comatching.comatching3.util.ResponseCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MatchRabbitMQUtil {

	private final RabbitTemplate rabbitTemplate;

	@Value("${rabbitmq.routing-keys.match-request}")
	private String matchRequestQueue;

	public MatchRabbitMQUtil(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	/**
	 * 메세지 브로커로 매칭 요청 & 응답 처리
	 * @param requestMsg : 매칭 리퀘스트 정보
	 * @param requestId : 매칭 요청 고유 id
	 * @return : 결과로 나온 유저의 uuid
	 */
	public MatchResponseMsg match(MatchRequestMsg requestMsg, String requestId) {
		CorrelationData correlationData = new CorrelationData(requestId);
		ParameterizedTypeReference<MatchResponseMsg> responseType = new ParameterizedTypeReference<MatchResponseMsg>(){};

		MatchResponseMsg response = rabbitTemplate.convertSendAndReceiveAsType(
			matchRequestQueue,
			requestMsg,
			(MessagePostProcessor) null,
			correlationData,
			responseType);

		if(response == null){
			throw new BusinessException(ResponseCode.NO_MATCH_RESPONSE);
		}

		log.info("[MatchRabbitMQUtil] match success!! enemyUuid = {}", response.getEnemyUuid());
		return response;
	}
}
