package comatching.comatching3.event.entity;

import comatching.comatching3.admin.entity.University;
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
    public DiscountEvent(LocalDateTime start, LocalDateTime end, University university, Integer discountRate) {
        this.start = start;
        this.end = end;
        this.university = university;
        this.discountRate = discountRate;
    }
}
