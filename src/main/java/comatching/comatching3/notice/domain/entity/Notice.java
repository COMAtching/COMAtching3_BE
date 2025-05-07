package comatching.comatching3.notice.domain.entity;

import comatching.comatching3.admin.entity.University;
import comatching.comatching3.util.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Entity
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    protected Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    protected University university;

    @NotNull
    protected String title;

    @NotNull
    protected String contents;
}
