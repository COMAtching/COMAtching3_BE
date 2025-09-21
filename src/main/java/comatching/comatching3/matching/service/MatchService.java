package comatching.comatching3.matching.service;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import comatching.comatching3.chat.service.ChatService;
import comatching.comatching3.event.entity.DiscountEvent;
import comatching.comatching3.event.entity.Event;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.history.entity.MatchingHistory;
import comatching.comatching3.history.repository.MatchingHistoryRepository;
import comatching.comatching3.matching.dto.messageQueue.MatchRequestMsg;
import comatching.comatching3.matching.dto.request.MatchReq;
import comatching.comatching3.matching.dto.response.MatchRes;
import comatching.comatching3.matching.dto.response.MatchingResult;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.RabbitMQ.MatchRabbitMQUtil;
import comatching.comatching3.util.RabbitMQ.UserCrudRabbitMQUtil;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

	private final int MAX_MATCH_COUNT = 10;
	private final int MAX_PICKED_COUNT = 5;

	private final UsersRepository usersRepository;
	private final MatchRabbitMQUtil matchRabbitMQUtil;
	private final SecurityUtil securityUtil;
	private final MatchingHistoryRepository matchingHistoryRepository;
	private final UserCrudRabbitMQUtil userCrudRabbitMQUtil;
	private final ChatService chatService;
	private final NoAiMatchingService noAiMatchingService;

	/**
	 * 괸리자 매칭 서비스 리퀘스트 메서드 메세지 브로커에게 리퀘스트를 publish
	 *
	 * @param matchReq : 매칭 요청을 위한 정보를 담은 Dto
	 * @return 요청 성공시 GEN-001
	 */
	@Transactional
	public MatchRes requestMatch(MatchReq matchReq) {
		Users applier = securityUtil.getCurrentUsersEntity();
		/*String requestId = UUID.randomUUID().toString();


		MatchRequestMsg requestMsg = new MatchRequestMsg();
		requestMsg.fromMatchReqAndUserAiFeature(matchReq, applier.getUserAiFeature(),
			applier.getUniversity().getUniversityName());
		requestMsg.updateWeight();

		// if (matchingHistoryRepository.countTodayByApplier(applier) >= MAX_MATCH_COUNT) {
		// 	throw new BusinessException(ResponseCode.MATCH_COUNT_OVER);
		// }

		//중복 유저 조회 및 브로커 메세지 반영
		Optional<List<MatchingHistory>> matchingHistories = matchingHistoryRepository.findByApplier(applier);
		matchingHistories.ifPresent(requestMsg::updateDuplicationListFromHistory);

		//메세지큐 전송 및 수신
		MatchResponseMsg responseMsg = matchRabbitMQUtil.match(requestMsg, requestId);

		//상대방 조회
		byte[] enemyUuid = UUIDUtil.uuidStringToBytes(responseMsg.getEnemyUuid());
		Users enemy = usersRepository.findUsersByUuidForUpdate(enemyUuid)
			.orElseThrow(() -> new BusinessException(ResponseCode.NO_ENEMY_AVAILABLE));*/

		//포인트 계산
		Long usePoint = calcPoint(matchReq);

		//유저저 포인트가 부족한지 체크
		if (usePoint > applier.getPoint()) {
			log.info("[MatchService] - user point lack applierPoint = {} calcPoint={}", applier.getPoint(), usePoint);
			throw new BusinessException(ResponseCode.INSUFFICIENT_POINT);
		}

		//포인트 차감
		applier.subtractPoint(usePoint);

		MatchingResult matchingResult = noAiMatchingService.noAiMatching(matchReq, usePoint);
		Users enemy = matchingResult.getEnemyUser();
		boolean refunded = matchingResult.isRefunded();
		//상대방 뽑힌 횟수 처리 & 뽑힌 횟수 체크 후 CSV 반영
		enemy.updatePickedCount();
		// if (enemy.getPickedCount() >= MAX_PICKED_COUNT) {
		// 	userCrudRabbitMQUtil.sendUserChange(enemy.getUserAiFeature(), UserCrudType.DELETE);
		// }

		//history 생성
		// createHistory(requestMsg, applier, enemy);
		noAiMatchingService.createHistory(matchReq, applier, enemy);

		//채팅방 생성
		Long chatRoomId = chatService.createChatRoom(applier, enemy);

		//응답 객체 생성
		MatchRes response = MatchRes.fromUsers(enemy);
		response.updateCurrentPoint(applier.getPoint());
		response.updateChatRoom(chatRoomId);
		response.updateRefunded(refunded);

		return response;
	}

	@Transactional
	public void updateEnemyPickedCount(byte[] uuid) {
		int retryCount = 3;  // 최대 재시도 횟수
		while (retryCount-- > 0) {
			try {
				// 사용자 조회
				Users enemy = usersRepository.findUsersByUuid(uuid)
					.orElseThrow(() -> new BusinessException(ResponseCode.NO_ENEMY_AVAILABLE));

				// pickedCount 업데이트
				enemy.updatePickedCount();  // 예: enemy.setPickedCount(enemy.getPickedCount() + 1);

				// 트랜잭션 종료 시점에 flush 되면서 버전 체크가 이루어짐
				// 버전 충돌이 일어나면 OptimisticLockingFailureException 발생

				// 성공 시 트랜잭션 커밋
				break;  // 성공하면 반복문을 종료
			} catch (OptimisticLockingFailureException e) {
				if (retryCount == 0) {
					// 최대 재시도 횟수 도달 시 예외를 던짐
					throw new BusinessException(ResponseCode.BAD_REQUEST);
				}
				// 재시도
				log.warn("Optimistic lock failure, retrying... ({} retries left)", retryCount);
			}
		}
	}

	/**
	 * 매칭 결과 저장 메서드
	 *
	 * @param matchReq
	 * @param applier
	 * @param enemy
	 */
	private void createHistory(MatchRequestMsg matchReq, Users applier, Users enemy) {
		MatchingHistory history = MatchingHistory.builder()
			.enemy(enemy)
			.applier(applier)
			.build();

		history.updateOptionsFromRequestMsg(matchReq);
		matchingHistoryRepository.save(history);
	}

	/**
	 * event 적용한 가격 계산 메서드
	 *
	 * @param event
	 * @param cost
	 * @return
	 */
	private Long calcEvent(Event event, Long cost) {
		if (event instanceof DiscountEvent discountEvent) {
			return Math.round(cost * (1 - discountEvent.getDiscountRate() / 100.0));
		}

		return cost;
	}

	/**
	 * 매칭 포인트 계산 메서드
	 *
	 * @param msg : 리퀘스트 정보
	 * @return : 요청된 매칭 포인트
	 */
	private Long calcPoint(MatchReq msg) {
		Long point = 1000L;

		if (msg.getSameMajorOption()) {
			point += 200;
		}

		if (!msg.getImportantOption().equals("UNSELECTED")) {
			point += 300;
		}

		return point;
	}
}
