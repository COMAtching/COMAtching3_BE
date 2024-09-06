package comatching.comatching3.util.RabbitMQ;

import java.util.UUID;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import comatching.comatching3.users.dto.messageQueue.CompensationMsg;
import comatching.comatching3.users.dto.messageQueue.UserCrudMsg;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.enums.UserCrudType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCrudRabbitMQUtil {

	@Value("${rabbitmq.routing-keys.user-crud-request}")
	private String userCrudQueue;

	private final RabbitTemplate rabbitTemplate;

	/**
	 * AI CSV에 반영하려는 데이터를 메세지큐로 보냄
	 * @param feature : CSV에 반영하려는 타겟 UsersAiFeature
	 * @param type : CSV 반영 방법 (UserCrudType 참고)
	 */
	public Boolean sendUserChange(UserAiFeature feature, UserCrudType type){
		rabbitTemplate.setReplyTimeout(10000);

		String requestId = UUID.randomUUID().toString();
		CorrelationData correlationData = new CorrelationData(requestId);
		ParameterizedTypeReference<CompensationMsg> responseType = new ParameterizedTypeReference<CompensationMsg>(){};
		UserCrudMsg userCrudMsg = new UserCrudMsg();
		userCrudMsg.updateFromUserAIFeatureAndType(type, feature);

		CompensationMsg response = 	rabbitTemplate.convertSendAndReceiveAsType(
			userCrudQueue,
			userCrudMsg,
			(MessagePostProcessor) null,
			correlationData,
			responseType);

		if(!response.getErrorCode().equals("GEN-000")){
			log.warn("[UserCrudResponse Error] errorCode={}  / errorMsg={}\n json = {}", response.getErrorCode(), response.getErrorMessage(), response.toJson());
			return false;
		}

		log.info("[UserCrudResponse Success]= errorCode: {} errorMsg: {}\njson = {}",response.getErrorCode(), response.getErrorMessage(), response.toJson());
		return true;
	}
}
