package comatching.comatching3.event.controller;

import comatching.comatching3.event.dto.res.EventRes;
import comatching.comatching3.event.service.EventUserService;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/auth/user")
public class UserEventController {

    private final EventUserService EventUserService;
    private final EventUserService eventUserService;

    /**
     * 유저 이벤트 조회 리스트 컨트롤러
     *
     * @param status - open : 현재 진행중이거나 진행될 소속 학교의 event
     * @return
     */
    @GetMapping("/events")
    public Response<List<EventRes>> requestActivatedEvent(@RequestParam InquiryStatus status) {

        List<EventRes> result = null;
        if (status == InquiryStatus.OPEN) {
            result = eventUserService.inquiryOpenEvent();
        }

        if (result == null) {
            throw new BusinessException(ResponseCode.WRONG_EVENT_STATUS);
        }

        return Response.ok(result);
    }

    @GetMapping("/event")
    public Response<EventRes> requestCurrentEVnet() {

        EventRes result = eventUserService.inquiryCurrentEvent();
        return Response.ok(result);
    }

}
