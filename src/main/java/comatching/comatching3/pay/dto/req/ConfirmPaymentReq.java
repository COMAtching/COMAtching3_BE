package comatching.comatching3.pay.dto.req;

import lombok.Getter;

@Getter
public class ConfirmPaymentReq {

	private Long amount;
	private String orderId;
	private String paymentKey;
}
