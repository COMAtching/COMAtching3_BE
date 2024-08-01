package comatching.comatching3.match.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import comatching.comatching3.match.dto.request.MatchReq;

@Service
public class MatchMessageService {
	private final RabbitTemplate rabbitTemplate;
	@Value("rabbitmq.exchanges.direct")
	private String directExchange;

	@Value("${rabbitmq.routing-keys.match-request}")
	private String matchRequestRoutingKey;

	MatchMessageService(RabbitTemplate rabbitTemplate){
		this.rabbitTemplate = rabbitTemplate;
	}

	public void sendMessageToMatchRequest(MatchReq req){
		rabbitTemplate.convertAndSend(directExchange, matchRequestRoutingKey, req);
	}

}
