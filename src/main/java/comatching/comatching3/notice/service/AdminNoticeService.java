package comatching.comatching3.notice.service;

import comatching.comatching3.admin.entity.University;
import comatching.comatching3.notice.domain.entity.Notice;
import comatching.comatching3.notice.domain.enums.NoticeType;
import comatching.comatching3.notice.dto.request.NoticeRegisterReq;
import comatching.comatching3.notice.repository.NoticeRepository;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNoticeService {

    private final NoticeRepository noticeRepository;
    private final SecurityUtil securityUtil;
    private final UsersRepository usersRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void addGeneralNotice(NoticeRegisterReq req) {

        Notice newNotice = Notice.builder()
                .title(req.title())
                .content(req.content())
                .postedAt(req.postedAt())
                .closedAt(req.closedAt())
                .noticeType(NoticeType.GENERAL)
                .build();

        noticeRepository.save(newNotice);

        University univ = securityUtil.getCurrentUsersEntity().getUniversity();
        List<Users> users = usersRepository.findAllUserByUniversityId(univ.getId());

        batchInsertNoticeConfirms(users, newNotice.getId());
    }

    private void batchInsertNoticeConfirms(List<Users> users, Long noticeId) {
        String sql = "INSERT INTO notice_confirm (user_id, notice_id, is_confirmed) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, users.get(i).getId());
                ps.setLong(2, noticeId);
                ps.setBoolean(3, false);
            }

            @Override
            public int getBatchSize() {
                return users.size();
            }
        });
    }

}
