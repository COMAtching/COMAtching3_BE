package comatching.comatching3.match.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.history.entity.MatchingHistory;
import comatching.comatching3.history.repository.MatchingHistoryRepository;
import comatching.comatching3.match.dto.messageQueue.MatchRequestMsg;
import comatching.comatching3.match.dto.messageQueue.MatchResponseMsg;
import comatching.comatching3.match.dto.request.MatchReq;
import comatching.comatching3.match.dto.response.MatchRes;
import comatching.comatching3.match.enums.AgeOption;
import comatching.comatching3.match.enums.ContactFrequencyOption;
import comatching.comatching3.users.dto.UserFeatureReq;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.Hobby;
import comatching.comatching3.users.enums.UserCrudType;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.RabbitMQ.MatchRabbitMQUtil;
import comatching.comatching3.util.RabbitMQ.UserCrudRabbitMQUtil;
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
	private final SecurityUtil securityUtil;
	private final MatchingHistoryRepository matchingHistoryRepository;
	private final UserCrudRabbitMQUtil userCrudRabbitMQUtil;

	/**
	 * 괸리자 매칭 서비스 리퀘스트 메서드
	 * 메세지 브로커에게 리퀘스트를 publish
	 * @param matchReq : 매칭 요청을 위한 정보를 담은 Dto
	 * @return 요청 성공시 GEN-001
	 *
	 */
	@Transactional
	public MatchRes requestMatch(MatchReq matchReq){
		String requestId = UUID.randomUUID().toString();
		log.info("{req-contactFrequency} = {}", matchReq.getContactFrequencyOption());
		MatchRequestMsg requestMsg = new MatchRequestMsg(matchReq);
		MatchResponseMsg responseMsg  = matchRabbitMQUtil.match(matchReq, requestId);

		log.info("{match-queues} = enemyId:{}", responseMsg.getUuid());

		//사용자 조회
		// todo : 조회 불가시 보상 처리
		byte[] enemyUuid = UUIDUtil.uuidStringToBytes(responseMsg.getUuid());
		Users enemy = usersRepository.findUsersByUuid(enemyUuid)
			.orElseThrow( () -> new BusinessException(ResponseCode.MATCH_GENERAL_FAIL));
		Users applier = securityUtil.getCurrentUsersEntity();

		//포인트 & pickMe 차감
		Integer usePoint = calcPoint(requestMsg);
		applier.subtractPoint(usePoint);
		enemy.updatePickMe(enemy.getPickMe() - 1);

		// fixme : sendUserChange 내부 익셉션으로 변경 필요
		if(enemy.getPoint() == 0){
			Boolean sendSuccess = userCrudRabbitMQUtil.sendUserChange(enemy.getUserAiFeature(), UserCrudType.DELETE);
			if(!sendSuccess){
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
		return response;
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

	public void testCrud(UserFeatureReq req){

		UserAiFeature userAiFeature = UserAiFeature.builder()
			.uuid(UUIDUtil.createUUID())
			.build();

		List<Hobby> hobbies = new ArrayList<>();
		for(String s : req.getHobby()){
			System.out.println(s);
			hobbies.add(Hobby.from(s));
		}

		userAiFeature.updateMbti(req.getMbti());
		userAiFeature.updateContactFrequency(req.getContactFrequency());
		userAiFeature.updateHobby(hobbies);
		userAiFeature.updateAge(req.getAge());
		userAiFeature.updateGender(req.getGender());
		userAiFeature.updateMajor(req.getMajor());
		userAiFeature.updateAdmissionYear(req.getAdmissionYear());

		userCrudRabbitMQUtil.sendUserChange(userAiFeature,UserCrudType.CREATE);
	}
}
