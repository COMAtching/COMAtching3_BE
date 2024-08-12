package comatching.comatching3.match.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.history.entity.MatchingHistory;
import comatching.comatching3.match.AgeOption;
import comatching.comatching3.match.ContactFrequencyOption;
import comatching.comatching3.match.dto.messageQueue.MatchRequestMsg;
import comatching.comatching3.match.dto.messageQueue.MatchResponseMsg;
import comatching.comatching3.match.dto.request.MatchReq;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.Hobby;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.RabbitMQ.MatchRabbitMQUtil;
import comatching.comatching3.util.RedisUtil;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MatchService {

	private final RedisUtil redisUtil;

	private final UsersRepository usersRepository;

	private final MatchRabbitMQUtil matchRabbitMQUtil;

	private final SecurityUtil securityUtil;


	MatchService(RedisUtil redisUtil, UsersRepository usersRepository, MatchRabbitMQUtil matchRabbitMQUtil, SecurityUtil securityUtil){
		this.redisUtil = redisUtil;
		this.usersRepository = usersRepository;
		this.matchRabbitMQUtil = matchRabbitMQUtil;
		this.securityUtil = securityUtil;
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
	@Transactional
	public Response<Void> requestMatch(MatchReq matchReq){
		String requestId = UUID.randomUUID().toString();
		MatchRequestMsg requestMsg = new MatchRequestMsg(matchReq, requestId);
		MatchResponseMsg responseMsg;

		try{
			redisUtil.putRedisValue(requestId,requestMsg);
			responseMsg = matchRabbitMQUtil.match(matchReq, requestId);
			MatchRequestMsg isRequestMsg = redisUtil.getRedisValue(responseMsg.getRequestId(), MatchRequestMsg.class);

			if(isRequestMsg == null || !isRequestMsg.equals(requestMsg)){
				throw new BusinessException(ResponseCode.MATCH_GENERAL_FAIL);
			}
		} catch(JsonProcessingException e){
			throw new BusinessException(ResponseCode.MATCH_GENERAL_FAIL);
		}

		/**
		 * 사용자 조회
		 * todo : 조회 불가시 보상 처리
		 */
		byte[] enemyUuid = UUIDUtil.uuidStringToBytes(requestMsg.getUuid());
		Users enemy = usersRepository.findUsersByUuid(enemyUuid)
			.orElseThrow( () -> new BusinessException(ResponseCode.MATCH_GENERAL_FAIL));
		Users applier = securityUtil.getCurrentUsersEntity();

		/**
		 * 포인트 & pickMe 차감
		 * todo : pickMe 소모시 csv 삭제 처리
 		 */
		Integer usePoint = calcPoint(requestMsg);
		applier.updatePoint(usePoint);
		enemy.updatePickMe(enemy.getPickMe() - 1);

		// history 생성
		MatchingHistory history = MatchingHistory.builder()
			.enemy(enemy)
			.applier(applier)
			.build();
		history.updateOptionsFromRequestMsg(requestMsg);

		return Response.ok();
	}

	/**
	 * 매칭 포인트 계산 메서드
	 * @param msg : 리퀘스트 정보
	 * @return : 요청된 매칭 포인트
	 */
	private Integer calcPoint(MatchRequestMsg msg){
		Integer point = 500;

		if(!msg.getAgeOption().equals(AgeOption.UNSELECTED)){
			point += 100;
		}

		if(!msg.getContactFrequencyOption().equals(ContactFrequencyOption.UNSELECTED)){
			point += 100;
		}

		if(!msg.getHobbyOption().get(0).equals(Hobby.UNSELECTED)){
			point += 100;
		}

		if(msg.getSameMajorOption()){
			point += 200;
		}

		return point;
	}

}
