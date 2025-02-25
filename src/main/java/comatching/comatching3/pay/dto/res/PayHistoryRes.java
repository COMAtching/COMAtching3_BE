package comatching.comatching3.pay.dto.res;

import comatching.comatching3.pay.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayHistoryRes {
	private String productName;
	private Long amount;
	private OrderStatus orderStatus;
	private String requestAt;
	private String approvedAt;
	private String cancelReason;
	private String tossPaymentMethod;
}
