package comatching.comatching3.util.RabbitMQ;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	 * CorrelationData를 가지고 ACK, NACK를 반환받고 메세지 큐에 잘 삽입되었는지 확인
	 * @param correlationData : publish한 Message의 CorrelationData
	 */
	public boolean checkAcknowledge(CorrelationData correlationData, String uuid) {
		try {
			if(!correlationData.getFuture().get(10, TimeUnit.SECONDS).isAck()){
				ReturnedMessage message = correlationData.getReturned();

				log.warn(message.getMessage().toString());
				log.warn("Reply Code: {}, Reply Text: {}, Exchange: {}, Routing Key: {}", message.getReplyCode(), message.getReplyText(), message.getExchange(), message.getRoutingKey());

				return false;
			}
		} catch(ExecutionException | InterruptedException | TimeoutException e){
			log.warn("RabbitMQ Ack/Nack를 시스템 문제로 확인되지 못했습니다!! uuid={} ", uuid);
			return false;
		}
		return true;
	}
}
