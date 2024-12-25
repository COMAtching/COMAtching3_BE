package comatching.comatching3.pay.dto.res;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public class TossPaymentRes {

	private String mId;
	private String lastTransactionKey;
	private String paymentKey;
	private String orderId;
	private Error error;
	private String orderName;
	private int taxExemptionAmount;
	private String status;
	private String requestedAt;
	private String approvedAt;
	private boolean useEscrow;
	private boolean cultureExpense;
	private CardInfo card;
	private Object virtualAccount;
	private Object transfer;
	private Object mobilePhone;
	private Object giftCertificate;
	private Object cashReceipt;
	private Object cashReceipts;
	private Object discount;
	private List<Cancel> cancels;
	private Object secret;
	private String type;
	private EasyPay easyPay;
	private String country;
	private Object failure;
	private boolean isPartialCancelable;
	private Receipt receipt;
	private Checkout checkout;
	private String currency;
	private int totalAmount;
	private int balanceAmount;
	private int suppliedAmount;
	private int vat;
	private int taxFreeAmount;
	private Map<String, Object> metadata;
	private String method;
	private String version;

	@Getter
	public static class CardInfo {
		private String issuerCode;
		private String acquirerCode;
		private String number;
		private int installmentPlanMonths;
		private boolean isInterestFree;
		private String interestPayer;
		private String approveNo;
		private boolean useCardPoint;
		private String cardType;
		private String ownerType;
		private String acquireStatus;
		private String receiptUrl;
		private int amount;
	}

	@Getter
	public static class EasyPay {
		private String provider;
		private int amount;
		private int discountAmount;
	}

	@Getter
	public static class Receipt {
		private String url;
	}

	@Getter
	public static class Checkout {
		private String url;
	}

	@Getter
	public static class Cancel {
		private String transactionKey;
		private String cancelReason;
		private int taxExemptionAmount;
		private String canceledAt;
		private int transferDiscountAmount;
		private int easyPayDiscountAmount;
		private String receiptKey;
		private int cancelAmount;
		private int taxFreeAmount;
		private int refundableAmount;
		private String cancelStatus;
		private String cancelRequestId;
	}

	public ErrorDetails getError() {
		if ("2022-11-16".equals(version)) {  // 버전 2
			return error != null ? new ErrorDetails(error.getCode(), error.getMessage(), error.getTraceId())
				: new ErrorDetails();
		}
		return new ErrorDetails();
	}

	@Getter
	public static class Error {
		private String code;
		private String message;
		private String traceId;

		// constructors, getters, setters
	}

	// 에러 정보를 담을 공통 클래스
	@Getter
	public static class ErrorDetails {
		private String code;
		private String message;
		private String traceId;

		// constructors, getters, setters
		public ErrorDetails() {
			this.code = "";
			this.message = "";
			this.traceId = null;
		}

		public ErrorDetails(String code, String message, String traceId) {
			this.code = code;
			this.message = message;
			this.traceId = traceId;
		}
	}
}

