package comatching.comatching3.pay.enums;

public enum OrderStatus {

	ORDER_REQUEST("orderRequest"),
	ORDER_CANCEL("orderCancel"),
	ORDER_ERROR("orderError"),
	ORDER_COMPLETE("orderComplete"),
	ORDER_REFUND("orderRefund");

	private final String status;

	OrderStatus(String status) {
		this.status = status;
	}
}
