package comatching.comatching3.event.entity;

import comatching.comatching3.admin.entity.University;
import comatching.comatching3.event.dto.res.EventRes;
import comatching.comatching3.util.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "event_type")
@Setter
@Getter
public abstract class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    protected University university;

    protected LocalDateTime start;

    protected LocalDateTime end;

    protected Boolean isActivate;

    abstract EventRes toEventRes();
}
