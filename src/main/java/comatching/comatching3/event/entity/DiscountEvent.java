package comatching.comatching3.event.entity;

import comatching.comatching3.admin.entity.University;
import comatching.comatching3.admin.enums.EventType;
import comatching.comatching3.event.dto.res.DiscountEventRes;
import comatching.comatching3.event.dto.res.EventRes;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("DISCOUNT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DiscountEvent extends Event {
    private Integer discountRate;

    @Builder
    public DiscountEvent(LocalDateTime start, LocalDateTime end, University university, Integer discountRate, Boolean isActivate) {
        this.start = start;
        this.end = end;
        this.university = university;
        this.discountRate = discountRate;
        this.isActivate = isActivate;
    }

    @Override
    public EventRes toEventRes() {
        return new DiscountEventRes(id, start, end, EventType.DISCOUNT, isActivate, discountRate);
    }
}