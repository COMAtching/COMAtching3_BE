package comatching.comatching3.event.controller;

import comatching.comatching3.event.dto.req.DiscountEventRegisterReq;
import comatching.comatching3.event.dto.res.EventRes;
import comatching.comatching3.event.service.AdminEventService;
import comatching.comatching3.util.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class AdminEventController {

    private final AdminEventService adminEventService;

    /**
     * 이벤트 생성 요청
     *
     * @param req
     * @return
     */
    @PostMapping("/auth/admin/event/discount")
    public Response registerDiscountEvent(@Validated @RequestBody DiscountEventRegisterReq req) {

        adminEventService.registerDiscountEvent(req);
        return Response.ok();
    }

    /**
     * 현재 존재하는 이벤트 조회 요청
     *
     * @return
     */
    @GetMapping("/auth/admin/event")
    public Response<List<EventRes>> inquiryEvent(@RequestParam String status) {

        List<EventRes> response = new ArrayList<>();

        if (status.equals("OPEN")) {
            response = adminEventService.inquiryEvent();
        } else if (status.equals("CLOSED")) {
            response = adminEventService.inquiryClosedEvent();
        }

        return Response.ok(response);
    }

}
