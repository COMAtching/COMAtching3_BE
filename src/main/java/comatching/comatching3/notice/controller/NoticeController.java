package comatching.comatching3.notice.controller;

import comatching.comatching3.notice.dto.response.NoticeRes;
import comatching.comatching3.notice.service.UserNoticeService;
import comatching.comatching3.util.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController("/auth/user/notice")
public class NoticeController {

    private final UserNoticeService userNoticeService;

    @GetMapping
    public Response getPostedNotice() {

        List<NoticeRes> res = userNoticeService.getPostedNotices();

        return Response.ok(res);
    }
}
