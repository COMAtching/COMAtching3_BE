package comatching.comatching3.util.RabbitMQ;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import comatching.comatching3.match.dto.messageQueue.MatchRequestMsg;
import comatching.comatching3.match.dto.response.MatchRes;

@Component
public class RabbitMQUtil {
	private final RabbitTemplate rabbitTemplate;

	@Value("${rabbitmq.exchanges.direct}")
	private String directExchange;

	@Value("${rabbitmq.routing-keys.match-request}")
	private String matchRequestRoutingKey;

	RabbitMQUtil(RabbitTemplate rabbitTemplate){
		this.rabbitTemplate = rabbitTemplate;
	}

	/**
	 * Match-Request 큐로 보내기 위한 유틸 메서드
	 * @param matchRequestMsg : Queue dto
	 */
	public void sendToMatchRequest(MatchRequestMsg matchRequestMsg){

		rabbitTemplate.convertAndSend(directExchange, matchRequestRoutingKey, matchRequestMsg);
	}


}
