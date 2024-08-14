package comatching.comatching3.util.RabbitMQ;

import static comatching.comatching3.util.RabbitMQ.RabbitMQUtil.*;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import comatching.comatching3.users.dto.messageQueue.UserCrudMsg;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.UserCrudType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserCrudRabbitMQUtil {

	@Value("${rabbitmq.routing-keys.user-crud-request}")
	private String userCrudQueue;

	@Value("${rabbitmq.routing-keys.user-crud-compensation}")
	private String userCrudCompensation;

	private final RabbitTemplate rabbitTemplate;

	public UserCrudRabbitMQUtil(RabbitTemplate rabbitTemplate){
		this.rabbitTemplate = rabbitTemplate;
	}

	/**
	 * AI CSV에 반영하려는 데이터를 메세지큐로 보냄
	 * @param users : CSV에 반영하려는 타겟 Users
	 * @param type : CSV 반영 방법 (UserCrudType 참고)
	 */
	public void sendUserChange(Users users, UserCrudType type){
		CorrelationData correlationData = new CorrelationData();
		UserCrudMsg userCrudMsg = UserCrudMsg.fromUserAIFeatureAndType(type, users.getUserAiFeature());

		rabbitTemplate.convertAndSend(userCrudQueue,userCrudMsg, correlationData);

		checkAcknowledge(correlationData, userCrudMsg.getUuid());
	}
}
