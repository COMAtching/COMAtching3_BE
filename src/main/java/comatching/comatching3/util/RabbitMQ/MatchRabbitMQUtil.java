package comatching.comatching3.util.RabbitMQ;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.match.dto.messageQueue.MatchRequestMsg;
import comatching.comatching3.match.dto.messageQueue.MatchResponseMsg;
import comatching.comatching3.match.dto.request.MatchReq;
import comatching.comatching3.util.ResponseCode;


@Component
public class MatchRabbitMQUtil {

	private final RabbitTemplate rabbitTemplate;
	private final ConcurrentHashMap<String, BlockingQueue<MatchResponseMsg>> responseMap = new ConcurrentHashMap<>();

	public MatchRabbitMQUtil(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	/**
	 * 매칭 요청을 MQ에 요청 & 응답  프로세스 수행
	 * 사용 queue : match-request & match-resposne
	 * @param matchReq : 매칭 리퀘스트 정보
	 * @param requestId : 매칭 요청 고유 id
	 * @return : 결과로 나온 유저의 uuid
	 */
	public MatchResponseMsg match(MatchReq matchReq, String requestId) {
		MatchRequestMsg requestMsg = new MatchRequestMsg(matchReq, requestId);

		// 메시지를 큐에 보냄
		rabbitTemplate.convertAndSend("match-request", requestMsg, new CorrelationData(requestId));

		// 응답 대기 큐를 생성하고 저장
		BlockingQueue<MatchResponseMsg> responseQueue = new LinkedBlockingQueue<>();
		responseMap.put(requestId, responseQueue);

		// 응답 대기 (타임아웃 설정)
		try{
			MatchResponseMsg response = responseQueue.poll(10, TimeUnit.SECONDS);
			if (response != null && response.getRequestId().equals(requestId)) {
				return response;
			}
			else {
				throw new BusinessException(ResponseCode.MATCH_GENERAL_FAIL);
			}
		}
		catch(InterruptedException e){
			throw new BusinessException(ResponseCode.MATCH_TIME_OVER);
		}
		finally {
			responseMap.remove(requestId);
		}
	}

	/**
	 * 매칭 응답 MQ 리스너 함수
	 * @param response response 큐의 응답
	 */
	@RabbitListener(queues = "match-response")
	public void handleResponse(MatchResponseMsg response){
		BlockingQueue<MatchResponseMsg> queue = responseMap.get(response.getRequestId());
		queue.offer(response);
	}
}
