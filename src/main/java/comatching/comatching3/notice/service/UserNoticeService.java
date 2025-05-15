package comatching.comatching3.notice.service;

import comatching.comatching3.notice.domain.entity.Notice;
import comatching.comatching3.notice.dto.response.NoticeRes;
import comatching.comatching3.notice.repository.NoticeRepository;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserNoticeService {

    private final SecurityUtil securityUtil;
    private final NoticeRepository noticeRepository;

    public List<NoticeRes> getPostedNotices() {
        List<Notice> notices = noticeRepository.findPostedNotice(LocalDateTime.now());

        List<NoticeRes> res = new ArrayList<>();
        for (Notice notice : notices) {
            res.add(notice.toResponse());
        }

        return res;
    }

}
