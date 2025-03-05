package comatching.comatching3.admin.controller;

import comatching.comatching3.admin.dto.request.DiscountEventRegisterReq;
import comatching.comatching3.admin.dto.response.EventRes;
import comatching.comatching3.admin.entity.event.Event;
import comatching.comatching3.admin.service.EventService;
import comatching.comatching3.util.Response;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * 이벤트 생성 요청
     * @param req
     * @return
     */
    @PostMapping("/admin/event/register/discount")
    public Response registerDiscountEvent(@Validated @RequestBody DiscountEventRegisterReq req){

        eventService.registerDiscountEvent(req);
        return Response.ok();
    }

    /**
     * 현재 존재하는 이벤트 조회 요청
     * @return
     */
    @GetMapping("/admin/event/inquiry")
    public Response<List<EventRes>> inquiryEvent(){
        return Response.ok(eventService.inquiryEvent());
    }

}
