package comatching.comatching3.pay.entity;

import comatching.comatching3.pay.enums.OrderStatus;
import comatching.comatching3.users.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orders {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;

	@OneToOne(mappedBy = "order")
	private TossPayment tossPayment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id")
	private Users users;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	private String product;

	private Long amount;

	private Long point;

	private String orderUuid;

	@Builder
	public Orders(TossPayment tossPayment, Users users, OrderStatus orderStatus, String product, Long amount, Long point,
		String orderUuid) {
		this.tossPayment = tossPayment;
		this.users = users;
		this.orderStatus = orderStatus;
		this.product = product;
		this.amount = amount;
		this.point = point;
		this.orderUuid = orderUuid;
	}

	public void setTossPayment(TossPayment tossPayment) {
		this.tossPayment = tossPayment;
	}

	public void updateOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

}
