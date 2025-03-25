package comatching.comatching3.pay.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import comatching.comatching3.pay.dto.req.ConfirmPaymentReq;
import comatching.comatching3.pay.dto.req.OrderReq;
import comatching.comatching3.pay.dto.res.OrderRes;
import comatching.comatching3.pay.service.PayService;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PayController {

	private final PayService payService;

	/**
	 * 주문 생성 메서드
	 *
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
	 *
	 * @param idempotencyKey    멱등키
	 * @param confirmPaymentReq 결제 정보
	 */
	@PostMapping("/confirm")
	public Response<Void> confirmPayment(@RequestHeader("Idempotency-Key") String idempotencyKey,
		@RequestBody ConfirmPaymentReq confirmPaymentReq) {
		boolean success = payService.confirm(idempotencyKey, confirmPaymentReq);
		if (!success)
			return Response.errorResponse(ResponseCode.PAYMENT_FAIL);

		System.out.println("confirm 결과" + success);
		return Response.ok();
	}
}
