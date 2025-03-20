package comatching.comatching3.event.service;

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

@Service
@RequiredArgsConstructor
public class EventUserService {

    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final SecurityUtil securityUtil;

    /**
     * 현재 유저에가 이벤트에 참여하는 것을 업데이트
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
}
