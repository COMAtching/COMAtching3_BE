package comatching.comatching3.match.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.match.dto.messageQueue.MatchRequestMsg;
import comatching.comatching3.match.dto.request.MatchReq;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.RabbitMQ.MatchRabbitMQUtil;
import comatching.comatching3.util.RabbitMQ.RabbitMQUtil;
import comatching.comatching3.util.RedisUtil;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MatchService {

	private final RedisUtil redisUtil;

	private final UsersRepository usersRepository;

	private final MatchRabbitMQUtil matchRabbitMQUtil;


	MatchService(RedisUtil redisUtil, UsersRepository usersRepository, MatchRabbitMQUtil matchRabbitMQUtil){
		this.redisUtil = redisUtil;
		this.usersRepository = usersRepository;
		this.matchRabbitMQUtil = matchRabbitMQUtil;
	}

	/**
	 * 매칭 서비스 리퀘스트 메서드
	 * 매칭 리퀘스트를 redis에 고유번호 부여 후 저장하고
	 * 메세지 브로커에게 리퀘스트를 publish
	 * @param matchReq : 매칭 요청을 위한 정보를 담은 Dto
	 * @return 요청 성공시 GEN-001
	 *
	 * todo : 매칭 히스토리 생성 및 포인트 차감 로직 추가
	 */
	public Response<Void> requestMatch(MatchReq matchReq){
		String requestId = UUID.randomUUID().toString();
		MatchRequestMsg msg = new MatchRequestMsg(matchReq, requestId);

		try{
			redisUtil.putRedisValue(requestId,msg);
			String enemyId = matchRabbitMQUtil.match(matchReq, requestId);

			log.info(enemyId);
			//Users enemy = usersRepository.findB(enemyId);
		} catch(JsonProcessingException e){
			throw new BusinessException(ResponseCode.MATCH_GENERAL_FAIL);
		}
		return Response.ok();
	}

}
