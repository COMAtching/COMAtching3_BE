package comatching.comatching3.event.service;

import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.enums.EventType;
import comatching.comatching3.event.dto.req.DiscountEventRegisterReq;
import comatching.comatching3.event.dto.res.EventRes;
import comatching.comatching3.event.entity.DiscountEvent;
import comatching.comatching3.event.entity.Event;
import comatching.comatching3.event.repository.EventRepository;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AdminEventService {

    private final SecurityUtil securityUtil;
    private final EventRepository eventRepository;

    private final JobLauncher jobLauncher;
    private final Job createEventParticipationJob;

    /**
     * 할인 이벤트 등록 메서드
     *
     * @param req
     */
    @Transactional
    public void registerDiscountEvent(DiscountEventRegisterReq req) {
        Admin admin = securityUtil.getAdminFromContext();

        DiscountEvent discountEvent = DiscountEvent.builder()
                .start(req.getStart())
                .end(req.getEnd())
                .university(admin.getUniversity())
                .discountRate(req.getDiscountRate())
                .build();

        log.info("start={} end={}", req.getStart(), req.getEnd());

        if (isEventDuplicate(discountEvent)) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }
        eventRepository.save(discountEvent);

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("eventId", discountEvent.getId())
                .addLong("universityId", admin.getUniversity().getId())
                .toJobParameters();

        try {
            jobLauncher.run(createEventParticipationJob, jobParameters);
        } catch (JobExecutionException e) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }


    }

    /**
     * 이벤트가 현재 등록된 이벤트들과 시간이 겹치는 확인
     *
     * @param event
     * @return 이벤트의 기간 중복 여부
     */
    private Boolean isEventDuplicate(Event event) {
        return eventRepository.existsOverlappingEvent(
                event.getUniversity(), event.getStart(), event.getEnd()
        );
    }

    /**
     * 관리자 - 현재 학교의 event 조회
     *
     * @return 현재 존재하는 event list
     */
    @Transactional
    public List<EventRes> inquiryEvent() {
        List<Event> eventList = eventRepository.findEventsByUniversity(securityUtil.getAdminFromContext().getUniversity());
        List<EventRes> response = new ArrayList<>();

        if (eventList == null) {
            throw new BusinessException(ResponseCode.NO_EVENT);
        }

        for (Event event : eventList) {
            EventRes eventRes = new EventRes();
            eventRes.setEnd(event.getEnd());
            eventRes.setStart(event.getStart());
            eventRes.setEventId(eventRes.getEventId());

            //할인 이벤트
            if (event instanceof DiscountEvent) {
                eventRes.setEventType(EventType.DISCOUNT);
                eventRes.setDiscountRate(((DiscountEvent) event).getDiscountRate());
            }
            response.add(eventRes);
        }

        return response;
    }

}
