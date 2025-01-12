package comatching.comatching3.pay.dto.req;

import lombok.Getter;

@Getter
public class SaveAmountReq {
	private String orderId;
	private Long amount;
}
