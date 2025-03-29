package comatching.comatching3.event.controller;

import comatching.comatching3.event.dto.req.DiscountEventRegisterReq;
import comatching.comatching3.event.dto.res.DiscountEventRes;
import comatching.comatching3.event.service.AdminEventService;
import comatching.comatching3.util.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/admin/event/register/discount")
    public Response registerDiscountEvent(@Validated @RequestBody DiscountEventRegisterReq req) {

        adminEventService.registerDiscountEvent(req);
        return Response.ok();
    }

    /**
     * 현재 존재하는 이벤트 조회 요청
     *
     * @return
     */
    @GetMapping("/admin/event/inquiry")
    public Response<List<DiscountEventRes>> inquiryEvent() {
        return Response.ok(adminEventService.inquiryEvent());
    }

}
