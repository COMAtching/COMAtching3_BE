package comatching.comatching3.charge.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import comatching.comatching3.pay.enums.OrderStatus;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.util.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChargeRequest extends BaseEntity {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    private String productName;

    @Setter
    private OrderStatus orderStatus;

    private Long price;

    private Long point;

    private LocalDateTime requestAt;

    @Setter
    private LocalDateTime approvedAt;

    @Setter
    private String cancelReason;

    private String orderId;

    private String realName;


    @Builder
    public ChargeRequest(Users users, String productName, OrderStatus orderStatus, Long price, Long point, LocalDateTime approvedAt, String cancelReason, String realName) {
        this.users = users;
        this.productName = productName;
        this.orderStatus = orderStatus;
        this.price = price;
        this.point = point;
        this.requestAt = LocalDateTime.now();
        this.approvedAt = approvedAt;
        this.cancelReason = cancelReason;
        this.orderId = UUID.randomUUID().toString().substring(0, 13);
        this.realName = realName;
    }
}
