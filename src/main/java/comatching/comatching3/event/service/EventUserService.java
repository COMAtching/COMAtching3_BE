package comatching.comatching3.event.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import comatching.comatching3.admin.entity.University;
import comatching.comatching3.event.dto.res.EventRes;
import comatching.comatching3.event.entity.DiscountEvent;
import comatching.comatching3.event.entity.Event;
import comatching.comatching3.event.entity.EventParticipation;
import comatching.comatching3.event.repository.EventParticipationRepository;
import comatching.comatching3.event.repository.EventRepository;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventUserService {

	private final RedisTemplate redisTemplate;
	private final EventRepository eventRepository;
	private final EventParticipationRepository eventParticipationRepository;
	private final SecurityUtil securityUtil;
	private final ObjectMapper objectMapper;

	/**
	 * 현재 유저가 이벤트에 참여하는 것을 업데이트
	 *
	 * @param eventId : 유저가 참여할 이벤트
	 *                이벤트 참여 시간 지날경우) EVT-004
	 *                이벤트 생성당시 가입이 안되었을 경우) EVT-003
	 */
	@Transactional
	public void participateEvent(Long eventId) {
		Users participant = securityUtil.getCurrentUsersEntity();

		EventParticipation eventParticipation = eventParticipationRepository.findEventParticipationByEventIdAndUserId(
				eventId, participant.getId())
			.orElseThrow(() -> new BusinessException(ResponseCode.EVENT_TIME_OVER));

		if (eventParticipation.getEvent().getEnd().isBefore(LocalDateTime.now())) {
			throw new BusinessException(ResponseCode.CANT_PARTICIPATE);
		}

		if (!eventParticipation.getParticipated()) {
			eventParticipation.participateEvent();
		}
	}

	/**
	 * 현재 학교에서 진행중이거나 진행 예정인 event 조회
	 *
	 * @return 위 조건을 만족하는 Event 응답 객체 리스트
	 */
	@Transactional
	public List<EventRes> inquiryOpenEvent() {
		University userUniversity = securityUtil.getCurrentUsersEntity().getUniversity();
		log.info("univerity = {}", userUniversity.getId());
		List<Event> events = eventRepository.findOngoingEventsByUniversity(userUniversity);

		log.info("Events inquiry: {}", events);
		List<EventRes> eventResList = new ArrayList<>();
		for (Event event : events) {
			log.info("Event class: {}", event.getClass());
			if (event instanceof DiscountEvent) {
				eventResList.add(((DiscountEvent)event).toEventRes());
			}
		}

		return eventResList;
	}

	@Transactional
	public EventRes inquiryCurrentEvent() {
		University userUniversity = securityUtil.getCurrentUsersEntity().getUniversity();

		Event event = eventRepository.findCurrentlyActiveEventByUniversity(LocalDateTime.now(), userUniversity)
			.orElseThrow(() -> new BusinessException(ResponseCode.NO_EVENT));

		Long eventId = event.getId();
		Long userId = securityUtil.getCurrentUsersEntity().getId();
		String redisKey = "eventParticipation:" + eventId + ":" + userId;

		// 1. Redis에서 조회
		Boolean participated = (Boolean)redisTemplate.opsForValue().get(redisKey);
		log.info("participated = {}", participated);

		if (participated == null) {
			log.info("Participation for {} not found", eventId);
			// 2. DB에서 원본 조회
			EventParticipation eventParticipation = eventParticipationRepository
				.findEventParticipationByEventIdAndUserId(eventId, userId)
				.orElseThrow(() -> new BusinessException(ResponseCode.WRONG_EVENT_STATUS));

			participated = eventParticipation.getParticipated();

			// 3. Redis에 캐시 (10분)
			redisTemplate.opsForValue().set(redisKey, participated, Duration.ofMinutes(10));
		}

		// 4. 참여 여부 확인
		if (Boolean.TRUE.equals(participated)) {
			throw new BusinessException(ResponseCode.NO_EVENT);
		}

		// 5. 이벤트 반환
		if (event instanceof DiscountEvent discountEvent) {
			return discountEvent.toEventRes();
		}

		throw new BusinessException(ResponseCode.NO_EVENT);
	}
}