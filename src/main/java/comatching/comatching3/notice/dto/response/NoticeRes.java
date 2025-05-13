package comatching.comatching3.notice.dto.response;


import java.time.LocalDateTime;

public record NoticeRes(Long id, String title, String content, LocalDateTime postedAt, LocalDateTime closedAt) {

}
