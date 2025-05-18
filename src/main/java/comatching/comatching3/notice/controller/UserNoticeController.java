package comatching.comatching3.notice.controller;

import comatching.comatching3.notice.dto.response.NoticeRes;
import comatching.comatching3.notice.service.AdminNoticeService;
import comatching.comatching3.util.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth/user/notice")
@RequiredArgsConstructor
public class UserNoticeController {

    private final AdminNoticeService adminNoticeService;

    @GetMapping
    public Response<List<NoticeRes>> inquiryEvent() {
        List<NoticeRes> res = adminNoticeService.getOpenNotices();
        
        return Response.ok(res);
    }
}
