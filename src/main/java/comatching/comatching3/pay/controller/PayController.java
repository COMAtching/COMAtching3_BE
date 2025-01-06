package comatching.comatching3.pay.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.pay.dto.req.ConfirmPaymentReq;
import comatching.comatching3.pay.dto.req.OrderReq;
import comatching.comatching3.pay.dto.req.SaveAmountReq;
import comatching.comatching3.pay.dto.res.OrderRes;
import comatching.comatching3.pay.dto.res.TossPaymentRes;
import comatching.comatching3.pay.service.PayService;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PayController {

	private final PayService payService;

	/**
	 * 결제의 금액을 세션에 임시저장
	 * 결제 과정에서 악의적으로 결제 금액이 바뀌는 것을 확인하는 용도
	 */
	@Deprecated
	// @PostMapping("/save-amount")
	public Response<Void> saveAmount(HttpSession session, @RequestBody SaveAmountReq saveAmountReq) {
		session.setAttribute(saveAmountReq.getOrderId(), saveAmountReq.getAmount());
		return Response.ok();
	}

	/**
	 * 결제 금액 검증
	 */
	@Deprecated
	// @PostMapping("/verify")
	public Response<Void> verifyAmount(HttpSession session, @RequestBody SaveAmountReq saveAmountReq) {

		String amount = "" + session.getAttribute(saveAmountReq.getOrderId());

		// 결제 전의 금액과 결제 후의 금액이 같은지 검증
		if (amount == null || amount.equals(saveAmountReq.getAmount())) {
			throw new BusinessException(ResponseCode.BAD_REQUEST);
		}

		// 검증에 사용했던 세션은 삭제
		session.removeAttribute(saveAmountReq.getOrderId());

		return Response.ok();
	}

	/**
	 * 주문 생성 메서드
	 * @param orderReq 주문 생성 요청 dto
	 * @return 주문 id
	 */
	@PostMapping("/order")
	public Response<OrderRes> makeOrder(@RequestBody OrderReq orderReq) {
		OrderRes orderRes = payService.makeOrder(orderReq);

		return Response.ok(orderRes);
	}

	/**
	 * 결제 승인 받기
	 * todo: 성공 실패 분기로 redirect 시킬지?
	 * @param idempotencyKey 멱등키
	 * @param confirmPaymentReq 결제 정보
	 */
	@PostMapping("/confirm")
	public Response<Void> confirmPayment(@RequestHeader("Idempotency-Key") String idempotencyKey, @RequestBody ConfirmPaymentReq confirmPaymentReq) {
		boolean success = payService.confirm(idempotencyKey, confirmPaymentReq);
		if (!success) return Response.errorResponse(ResponseCode.PAYMENT_FAIL);

		return Response.ok();
	}
}
