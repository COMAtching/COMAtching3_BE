package comatching.comatching3.util.RabbitMQ;

import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import comatching.comatching3.users.dto.messageQueue.CompensationMsg;
import comatching.comatching3.users.dto.messageQueue.UserCrudMsg;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.UserCrudType;
import comatching.comatching3.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCrudRabbitMQUtil {

	@Value("${rabbitmq.routing-keys.user-crud-request}")
	private String userCrudQueue;

	@Value("${rabbitmq.routing-keys.user-crud-compensation}")
	private String userCrudCompensation;

	private final RabbitTemplate rabbitTemplate;
	private final RabbitMQUtil rabbitMQUtil;
	private final UsersRepository usersRepository;

	/**
	 * AI CSV에 반영하려는 데이터를 메세지큐로 보냄
	 * @param users : CSV에 반영하려는 타겟 Users
	 * @param type : CSV 반영 방법 (UserCrudType 참고)
	 */
	public Boolean sendUserChange(Users users, UserCrudType type){

		String requestId = UUID.randomUUID().toString();
		CorrelationData correlationData = new CorrelationData(requestId);
		UserCrudMsg userCrudMsg = new UserCrudMsg();
		userCrudMsg.updateFromUserAIFeatureAndType(type, users.getUserAiFeature());

		int sendAttempt = 0;
		while(sendAttempt < 3){

			rabbitTemplate.convertAndSend(userCrudQueue,userCrudMsg, correlationData);

			if(rabbitMQUtil.checkAcknowledge(correlationData, userCrudMsg.getUuid())){
				return true;
			}
			sendAttempt++;
		}

		return false;
	}

	@RabbitListener(queues = "#{userCrudCompensation}")
	public void handleCompensationMessage(CompensationMsg msg){

	}
}
