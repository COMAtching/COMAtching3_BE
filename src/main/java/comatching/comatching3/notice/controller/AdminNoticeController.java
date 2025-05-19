package comatching.comatching3.notice.controller;

import comatching.comatching3.notice.dto.request.NoticeRegisterReq;
import comatching.comatching3.notice.dto.response.NoticeRes;
import comatching.comatching3.notice.service.AdminNoticeService;
import comatching.comatching3.util.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public Response<List<NoticeRes>> getOpenNotice(@RequestParam String state) {

        List<NoticeRes> res = new ArrayList<>();
        if (state.equals("OPEN")) {
            res = adminNoticeService.getOpenNotices();

        } else if (state.equals("HISTORY")) {
            res = adminNoticeService.getClosedNotices();
        }

        return Response.ok(res);
    }
}
