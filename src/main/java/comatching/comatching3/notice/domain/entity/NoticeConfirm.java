package comatching.comatching3.notice.domain.entity;

import comatching.comatching3.users.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class NoticeConfirm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_confirm_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    private Boolean isConfirmed = false;

    public void confirmNotice() {
        this.isConfirmed = true;
    }
}
