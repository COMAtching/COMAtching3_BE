package comatching.comatching3.matching.service;

import comatching.comatching3.util.Idempotent.Idempotent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.history.entity.MatchingHistory;
import comatching.comatching3.history.repository.MatchingHistoryRepository;
import comatching.comatching3.matching.dto.cache.CodeCheckInfo;
import comatching.comatching3.matching.dto.messageQueue.MatchRequestMsg;
import comatching.comatching3.matching.dto.messageQueue.MatchResponseMsg;
import comatching.comatching3.matching.dto.request.AdminMatchReq;
import comatching.comatching3.matching.dto.request.MatchReq;
import comatching.comatching3.matching.dto.response.MatchRes;
import comatching.comatching3.matching.enums.AgeOption;
import comatching.comatching3.matching.enums.ContactFrequencyOption;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.HobbyEnum;
import comatching.comatching3.users.enums.UserCrudType;
import comatching.comatching3.users.repository.HobbyRepository;
import comatching.comatching3.users.repository.UserAiFeatureRepository;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.RabbitMQ.MatchRabbitMQUtil;
import comatching.comatching3.util.RabbitMQ.UserCrudRabbitMQUtil;
import comatching.comatching3.util.RedisUtil;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

	private final UsersRepository usersRepository;
	private final MatchRabbitMQUtil matchRabbitMQUtil;
	private final HobbyRepository hobbyRepository;
	private final SecurityUtil securityUtil;
	private final MatchingHistoryRepository matchingHistoryRepository;
	private final UserCrudRabbitMQUtil userCrudRabbitMQUtil;
	private final UserAiFeatureRepository userAiFeatureRepository;
	private final RedisUtil redisUtil;

	/**
	 * 괸리자 매칭 서비스 리퀘스트 메서드
	 * 메세지 브로커에게 리퀘스트를 publish
	 * @param matchReq : 매칭 요청을 위한 정보를 담은 Dto
	 * @return 요청 성공시 GEN-001
	 *
	 */
	@Idempotent
	@Transactional
	public MatchRes requestMatch(MatchReq matchReq){
		String requestId = UUID.randomUUID().toString();
		Users applier = securityUtil.getCurrentUsersEntity();
		String applierUuid = UUIDUtil.bytesToHex(applier.getUserAiFeature().getUuid());

		MatchRequestMsg requestMsg = new MatchRequestMsg();
		requestMsg.fromMatchReqAndUserAiFeature(matchReq, applier.getUserAiFeature());

		//중복 유저 조회
		Optional<List<MatchingHistory>> matchingHistories = matchingHistoryRepository.findByApplier(applier);
		Boolean isEmpty = matchingHistories.isEmpty();

		if(isEmpty.booleanValue()){
			requestMsg.updateNoDuplication();
		}
		else{
			requestMsg.updateDuplicationListFromHistory(matchingHistories.get());
		}

		MatchResponseMsg responseMsg  = matchRabbitMQUtil.match(requestMsg, requestId);

		//상대방 조회
		byte[] enemyUuid = UUIDUtil.uuidStringToBytes(responseMsg.getEnemyUuid());
		Users enemy = usersRepository.findUsersByUuid(enemyUuid)
			.orElseThrow( () -> new BusinessException(ResponseCode.NO_ENEMY_AVAILABLE));

		//포인트 & pickMe 차감
		Long usePoint = calcPoint(matchReq);

		if(usePoint > applier.getPoint()){
			throw new BusinessException(ResponseCode.INSUFFICIENT_POINT);
		}

		applier.subtractPoint(usePoint);
		if(applier.getPickMe() <= 0){
			userCrudRabbitMQUtil.sendUserChange(applier.getUserAiFeature(), UserCrudType.CREATE);
		}
		applier.updatePickMe(applier.getPickMe() + 1);
		enemy.updatePickMe(enemy.getPickMe() - 1);
		if(enemy.getPickMe() <= 0){
			Boolean isSuccess = userCrudRabbitMQUtil.sendUserChange(enemy.getUserAiFeature(), UserCrudType.DELETE);
			if(!isSuccess){
				log.error("매칭 enemy 정보 AI 반영 에러 enemyUuid =  {} / enemyId = {}", responseMsg.getEnemyUuid(), enemy.getId());
				throw new BusinessException(ResponseCode.MATCH_GENERAL_FAIL);
			}
		}

		//history 생성
		MatchingHistory history = MatchingHistory.builder()
			.enemy(enemy)
			.applier(applier)
			.build();
		history.updateOptionsFromRequestMsg(requestMsg);
		matchingHistoryRepository.save(history);

		MatchRes response = MatchRes.fromUsers(enemy);
		response.updateCurrentPoint(applier.getPoint());
		log.info("[MatchService] - Match Process Success!! applierUuid = {}, enemyUuid = {}", applierUuid, UUIDUtil.bytesToHex(enemyUuid));
		return response;
	}

	@Transactional
	public MatchRes requestAdminMatch(AdminMatchReq req){
		try{
			String requestId = UUID.randomUUID().toString();
			CodeCheckInfo codeCheckInfo = redisUtil.getRedisValue(req.getCode(), CodeCheckInfo.class);

			if(codeCheckInfo == null){
				throw new BusinessException(ResponseCode.MATCH_CODE_CHECK_FAIL);
			}

			Users applier = usersRepository.findById(codeCheckInfo.getUserId())
				.orElseThrow(() -> {
					log.info("[MatchService - requestAdminMatch] - 매치코드가 인증된 유저를 찾지 못했습니다 applier.id = {}", codeCheckInfo.getUserId());
					return new BusinessException(ResponseCode.MATCH_GENERAL_FAIL);
				});

			String applierUuid = UUIDUtil.bytesToHex(applier.getUserAiFeature().getUuid());

			MatchRequestMsg requestMsg = new MatchRequestMsg();
			requestMsg.fromAdminMatchReqAndUserAiFeature(req, applier.getUserAiFeature());

			//중복 유저 조회
			Optional<List<MatchingHistory>> matchingHistories = matchingHistoryRepository.findByApplier(applier);
			Boolean isEmpty = matchingHistories.isEmpty();

			if(isEmpty.booleanValue()){
				requestMsg.updateNoDuplication();
			}
			else{
				requestMsg.updateDuplicationListFromHistory(matchingHistories.get());
			}

			MatchResponseMsg responseMsg  = matchRabbitMQUtil.match(requestMsg, requestId);

			//사용자 조회
			byte[] enemyUuid = UUIDUtil.uuidStringToBytes(responseMsg.getEnemyUuid());
			Users enemy = usersRepository.findUsersByUuid(enemyUuid)
				.orElseThrow( () -> new BusinessException(ResponseCode.NO_ENEMY_AVAILABLE));

			//포인트 & pickMe 차감
			Long usePoint = calcPoint(req);

			if(usePoint > applier.getPoint()){
				throw new BusinessException(ResponseCode.INSUFFICIENT_POINT);
			}

			applier.subtractPoint(usePoint);
			if(applier.getPickMe() <= 0){
				userCrudRabbitMQUtil.sendUserChange(applier.getUserAiFeature(), UserCrudType.CREATE);
			}
			applier.updatePickMe(applier.getPickMe() + 1);
			enemy.updatePickMe(enemy.getPickMe() - 1);
			if(enemy.getPickMe() == 0){
				Boolean isSuccess = userCrudRabbitMQUtil.sendUserChange(enemy.getUserAiFeature(), UserCrudType.DELETE);
				if(!isSuccess){
					log.error("[MatchService - requestAdminMatch] - 매칭 enemy 정보 AI 반영 에러 enemyUuid =  {} / enemyId = {}", responseMsg.getEnemyUuid(), enemy.getId());
					throw new BusinessException(ResponseCode.MATCH_GENERAL_FAIL);
				}
			}

			//history 생성
			MatchingHistory history = MatchingHistory.builder()
				.enemy(enemy)
				.applier(applier)
				.build();
			history.updateOptionsFromRequestMsg(requestMsg);
			matchingHistoryRepository.save(history);

			MatchRes response = MatchRes.fromUsers(enemy);
			response.updateCurrentPoint(applier.getPoint());
			log.info("[MatchService - requestAdminMatch] - Match Process Success!! applierUuid = {}, enemyUuid = {}", applierUuid, UUIDUtil.bytesToHex(enemyUuid));
			return response;

		} catch (JsonProcessingException e ){
			throw new BusinessException(ResponseCode.MATCH_GENERAL_FAIL);
		}
	}

	/**
	 * 매칭 포인트 계산 메서드
	 * @param msg : 리퀘스트 정보
	 * @return : 요청된 매칭 포인트
	 */
	private Long calcPoint(MatchReq msg){
		Long point = 500L;

		if(!msg.getAgeOption().equals(AgeOption.UNSELECTED)){
			point += 100;
		}

		if(!msg.getContactFrequencyOption().equals(ContactFrequencyOption.UNSELECTED)){
			point += 100;
		}

		if(!msg.getHobbyEnumOption().get(0).equals(HobbyEnum.UNSELECTED)){
			point += 100;
		}

		if(msg.getSameMajorOption()){
			point += 200;
		}

		return point;
	}

	/**
	 * 매칭 포인트 계산 메서드
	 * @param msg : 리퀘스트 정보
	 * @return : 요청된 매칭 포인트
	 */
	private Long calcPoint(AdminMatchReq msg){
		Long point = 500L;

		if(!msg.getAgeOption().equals(AgeOption.UNSELECTED)){
			point += 100;
		}

		if(!msg.getContactFrequencyOption().equals(ContactFrequencyOption.UNSELECTED)){
			point += 100;
		}

		if(!msg.getHobbyEnumOption().get(0).equals(HobbyEnum.UNSELECTED)){
			point += 100;
		}

		if(msg.getSameMajorOption()){
			point += 200;
		}

		return point;
	}
}
