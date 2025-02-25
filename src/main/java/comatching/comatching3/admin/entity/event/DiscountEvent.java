package comatching.comatching3.admin.entity.event;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("DISCOUNT")
@Getter
@Setter
public class DiscountEvent extends Event{
    private Integer discountRate;
}
