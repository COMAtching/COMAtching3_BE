package comatching.comatching3.admin.service;

import comatching.comatching3.admin.dto.request.DiscountEventRegisterReq;
import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.entity.event.DiscountEvent;
import comatching.comatching3.admin.entity.event.Event;
import comatching.comatching3.admin.enums.EventType;
import comatching.comatching3.admin.repository.EventRepository;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventService {

    private final SecurityUtil securityUtil;
    private final EventRepository eventRepository;

    public void registerDiscountEvent(DiscountEventRegisterReq req) {
        Admin admin = securityUtil.getAdminFromContext();

        DiscountEvent discountEvent = new DiscountEvent();
        discountEvent.setUniversity(admin.getUniversity());
        discountEvent.setDiscountRate(req.getDiscountRate());

        Boolean isEventPeriodDuplicate = isEventDuplicate(discountEvent);

        if(isEventPeriodDuplicate){
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }
        eventRepository.save(discountEvent);
    }

    /**
     * 이벤트가 현재 등록된 이벤트들과 시간이 겹치는 확인
     * @param event
     * @return 이벤트의 기간 중복 여부
     */
    private Boolean isEventDuplicate(Event event) {
        return eventRepository.existsOverlappingEvent(
                event.getUniversity(), event.getStart(), event.getEnd()
        );
    }

}
