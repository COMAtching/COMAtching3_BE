package comatching.comatching3.notice.domain.entity;

import comatching.comatching3.admin.entity.University;
import comatching.comatching3.notice.dto.response.NoticeRes;
import comatching.comatching3.util.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
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

    public NoticeRes toResponse() {
        return new NoticeRes(id, title, content, postedAt, closedAt);
    }
}
