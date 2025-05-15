package comatching.comatching3.notice.controller;

import comatching.comatching3.notice.dto.request.NoticeRegisterReq;
import comatching.comatching3.notice.dto.response.NoticeRes;
import comatching.comatching3.notice.service.AdminNoticeService;
import comatching.comatching3.util.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/admin/notice")
public class AdminNoticeController {

    private final AdminNoticeService adminNoticeService;

    @PostMapping
    public Response addNotice(@RequestBody NoticeRegisterReq notice) {
        adminNoticeService.addGeneralNotice(notice);

        return Response.ok();
    }


    @DeleteMapping
    public Response deleteNotice(@RequestParam("id") long id) {
        adminNoticeService.cancelNotice(id);

        return Response.ok();
    }

    @GetMapping
    public Response getOpenNotice(@RequestParam String state) {

        if (state.equals("OPEN")) {
            List<NoticeRes> res = adminNoticeService.getOpenNotices();
            return Response.ok(res);
        } else if (state.equals("HISTORY")) {
            List<NoticeRes> res = adminNoticeService.getClosedNotices();
        }

        return Response.ok();
    }
}
