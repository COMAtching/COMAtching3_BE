package comatching.comatching3.event.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventUserService {

    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final SecurityUtil securityUtil;

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

        EventParticipation eventParticipation = eventParticipationRepository.findEventParticipationByEventIdAndUserId(eventId, participant.getId())
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
        List<Event> events = eventRepository.findOngoingEventsByUniversity(userUniversity);

        List<EventRes> eventResList = new ArrayList<>();
        for (Event event : events) {
            if (event instanceof DiscountEvent) {
                DiscountEvent discountEvent = (DiscountEvent) event;
                eventResList.add(discountEvent.toEventRes());
            }
        }

        return eventResList;
    }
}