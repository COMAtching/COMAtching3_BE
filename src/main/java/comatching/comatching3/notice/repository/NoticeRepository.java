package comatching.comatching3.notice.repository;

import comatching.comatching3.notice.domain.entity.Notice;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("SELECT n FROM Notice n WHERE n.closedAt > :now AND n.cancelled = false")
    List<Notice> findOpenNotice(@Param("now") LocalDateTime now);

    @Query("SELECT n FROM Notice n WHERE n.closedAt < :now AND n.cancelled = false")
    List<Notice> findCloseNotice(@Param("now") LocalDateTime now);

}
