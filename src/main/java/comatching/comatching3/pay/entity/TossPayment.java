package comatching.comatching3.pay.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TossPayment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "toss_payment_id")
	private Long id;

	private String tossPaymentKey;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Orders order;

	private long totalAmount;

	private String tossPaymentMethod;

	private String tossPaymentStatus;

	private LocalDateTime requestedAt;

	private LocalDateTime approvedAt;

	private String cancelReason = "Not Canceled";

	@Builder
	public TossPayment(String tossPaymentKey, Orders order, long totalAmount,
		String tossPaymentMethod, String tossPaymentStatus, LocalDateTime requestedAt,
		LocalDateTime approvedAt) {
		this.tossPaymentKey = tossPaymentKey;
		this.order = order;
		this.totalAmount = totalAmount;
		this.tossPaymentMethod = tossPaymentMethod;
		this.tossPaymentStatus = tossPaymentStatus;
		this.requestedAt = requestedAt;
		this.approvedAt = approvedAt;
	}

	public void updateTossPaymentKey(String tossPaymentKey) {
		this.tossPaymentKey = tossPaymentKey;
	}

	public void updateTotalAmount(long totalAmount) {
		this.totalAmount = totalAmount;
	}

	public void updateTossPaymentMethod(String tossPaymentMethod) {
		this.tossPaymentMethod = tossPaymentMethod;
	}

	public void updateTossPaymentStatus(String tossPaymentStatus) {
		this.tossPaymentStatus = tossPaymentStatus;
	}

	public void updateRequestedAt(LocalDateTime requestedAt) {
		this.requestedAt = requestedAt;
	}

	public void updateApprovedAt(LocalDateTime approvedAt) {
		this.approvedAt = approvedAt;
	}

	public void updateCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
}
