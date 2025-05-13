package comatching.comatching3.notice.repository;

import comatching.comatching3.notice.domain.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    
}
