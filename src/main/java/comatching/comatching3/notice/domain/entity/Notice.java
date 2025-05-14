package comatching.comatching3.notice.domain.entity;

import comatching.comatching3.admin.entity.University;
import comatching.comatching3.notice.domain.enums.NoticeType;
import comatching.comatching3.notice.dto.response.NoticeRes;
import comatching.comatching3.util.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    private University university;

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private LocalDateTime postedAt;

    @NotNull
    private LocalDateTime closedAt;

    @NotNull
    private NoticeType noticeType;

    private Boolean cancelled = Boolean.FALSE;

    public NoticeRes toResponse() {
        return new NoticeRes(id, title, content, postedAt, closedAt);
    }

    public void cancel() {
        this.cancelled = Boolean.TRUE;
    }

    @Builder
    public Notice(String title, String content, LocalDateTime postedAt, LocalDateTime closedAt, NoticeType noticeType, University university) {
        this.title = title;
        this.content = content;
        this.postedAt = postedAt;
        this.closedAt = closedAt;
        this.noticeType = noticeType;
        this.university = university;
    }
}
