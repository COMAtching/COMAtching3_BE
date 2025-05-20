package comatching.comatching3.matching.service;

import comatching.comatching3.chat.service.ChatService;
import comatching.comatching3.event.entity.DiscountEvent;
import comatching.comatching3.event.entity.Event;
import comatching.comatching3.event.repository.EventRepository;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.history.entity.MatchingHistory;
import comatching.comatching3.history.repository.MatchingHistoryRepository;
import comatching.comatching3.matching.dto.messageQueue.MatchRequestMsg;
import comatching.comatching3.matching.dto.messageQueue.MatchResponseMsg;
import comatching.comatching3.matching.dto.request.MatchReq;
import comatching.comatching3.matching.dto.response.MatchRes;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.UserCrudType;
import comatching.comatching3.users.repository.UserAiFeatureRepository;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.RabbitMQ.MatchRabbitMQUtil;
import comatching.comatching3.util.RabbitMQ.UserCrudRabbitMQUtil;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    private final UsersRepository usersRepository;
    private final MatchRabbitMQUtil matchRabbitMQUtil;
    private final SecurityUtil securityUtil;
    private final MatchingHistoryRepository matchingHistoryRepository;
    private final UserCrudRabbitMQUtil userCrudRabbitMQUtil;
    private final UserAiFeatureRepository userAiFeatureRepository;
    private final ChatService chatService;
    private final EventRepository eventRepository;

    /**
     * 괸리자 매칭 서비스 리퀘스트 메서드 메세지 브로커에게 리퀘스트를 publish
     *
     * @param matchReq : 매칭 요청을 위한 정보를 담은 Dto
     * @return 요청 성공시 GEN-001
     */
    @Transactional
    public MatchRes requestMatch(MatchReq matchReq) {
        String requestId = UUID.randomUUID().toString();
        Users applier = securityUtil.getCurrentUsersEntity();
        String applierUuid = UUIDUtil.bytesToHex(applier.getUserAiFeature().getUuid());

        MatchRequestMsg requestMsg = new MatchRequestMsg();
        requestMsg.fromMatchReqAndUserAiFeature(matchReq, applier.getUserAiFeature(),
                applier.getUniversity().getUniversityName());
        requestMsg.updateWeight();

        //중복 유저 조회 및 브로커 메세지 반영
        Optional<List<MatchingHistory>> matchingHistories = matchingHistoryRepository.findByApplier(applier);
        matchingHistories.ifPresent(requestMsg::updateDuplicationListFromHistory);

        MatchResponseMsg responseMsg = matchRabbitMQUtil.match(requestMsg, requestId);

        //상대방 조회
        byte[] enemyUuid = UUIDUtil.uuidStringToBytes(responseMsg.getEnemyUuid());
        Users enemy = usersRepository.findUsersByUuid(enemyUuid)
                .orElseThrow(() -> new BusinessException(ResponseCode.NO_ENEMY_AVAILABLE));

        //상대방 뽑힌 횟수 처리
        enemy.updatePickedCount();
        if (enemy.getPickedCount() >= 5) {
            userCrudRabbitMQUtil.sendUserChange(enemy.getUserAiFeature(), UserCrudType.DELETE);
        }

        //포인트 계산
        Long usePoint = calcPoint(matchReq);
        Optional<Event> discountEventOpt = eventRepository.findCurrentlyActiveEventByUniversity(
                LocalDateTime.now(), applier.getUniversity());

        //할인 이벤트가 존재한다면 적용
        if (discountEventOpt.isPresent() && discountEventOpt.get() instanceof DiscountEvent discountEvent) {
            int discountRate = discountEvent.getDiscountRate();
            usePoint = usePoint * (100 - discountRate) / 100;
        }

        //유저저 포인트가 부족한지 체크
        if (usePoint > applier.getPoint()) {
            throw new BusinessException(ResponseCode.INSUFFICIENT_POINT);
        }

        //포인트 차감
        applier.subtractPoint(usePoint);

        //matching history 생성
        MatchingHistory history = MatchingHistory.builder()
                .enemy(enemy)
                .applier(applier)
                .build();
        history.updateOptionsFromRequestMsg(requestMsg);
        matchingHistoryRepository.save(history);

        //채팅방 생성
        Long chatRoomId = chatService.createChatRoom(applier, enemy);

        //응답 객체 생성
        MatchRes response = MatchRes.fromUsers(enemy);
        response.updateCurrentPoint(applier.getPoint());
        response.updateChatRoom(chatRoomId);

        log.info("[MatchService] - Match Process Success!! applierUuid = {}, enemyUuid = {} ", applierUuid,
                UUIDUtil.bytesToHex(enemyUuid));

        return response;
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

//        if (!msg.getAgeOption().equals(AgeOption.UNSELECTED)) {
//            point += 100;
//        }
//
//        if (!msg.getContactFrequencyOption().equals(ContactFrequencyOption.UNSELECTED)) {
//            point += 100;
//        }
//
//        if (!msg.getHobbyEnumOption().get(0).equals(HobbyEnum.UNSELECTED)) {
//            point += 100;
//        }

        if (msg.getSameMajorOption()) {
            point += 200;
        }

        if (!msg.getImportantOption().equals("UNSELECTED")) {
            point += 300;
        }

        return point;
    }
}
