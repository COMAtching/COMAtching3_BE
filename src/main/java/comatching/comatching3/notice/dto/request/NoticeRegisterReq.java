package comatching.comatching3.notice.dto.request;

import java.time.LocalDateTime;

public record NoticeRegisterReq(String title, String content, LocalDateTime postedAt, LocalDateTime closedAt) {

}
