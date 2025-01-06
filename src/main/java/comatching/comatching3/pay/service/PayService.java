package comatching.comatching3.pay.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import comatching.comatching3.config.TossPaymentConfig;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.exception.TossPaymentException;
import comatching.comatching3.exception.TossPaymentExceptionDto;
import comatching.comatching3.history.entity.PointHistory;
import comatching.comatching3.history.enums.PointHistoryType;
import comatching.comatching3.history.repository.PointHistoryRepository;
import comatching.comatching3.pay.dto.req.ConfirmPaymentReq;
import comatching.comatching3.pay.dto.req.OrderReq;
import comatching.comatching3.pay.dto.res.OrderRes;
import comatching.comatching3.pay.dto.res.TossPaymentRes;
import comatching.comatching3.pay.entity.Orders;
import comatching.comatching3.pay.entity.TossPayment;
import comatching.comatching3.pay.enums.OrderStatus;
import comatching.comatching3.pay.repository.OrderRepository;
import comatching.comatching3.pay.repository.TossPaymentRepository;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.security.SecurityUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayService {

	private final SecurityUtil securityUtil;
	private final OrderRepository orderRepository;
	private final TossPaymentRepository tossPaymentRepository;
	private final PayErrorService payErrorService;
	private final PayRedisService payRedisService;
	private final PointHistoryRepository pointHistoryRepository;

	//테스트용
	// private final UsersRepository usersRepository;

	@Value("${payment.toss.secret-key}")
	private String secretKey;

	@Transactional
	public OrderRes makeOrder(OrderReq orderReq) {
		Users user = securityUtil.getCurrentUsersEntity();
		// Users user = usersRepository.findBySocialId("3490175542")
		// 	.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

		String product = orderReq.getProductName();
		Long amount = orderReq.getAmount();

		Orders order = makeOrderEntity(user, product, amount);

		TossPayment tossPayment = TossPayment.builder()
			.order(order)
			.build();
		tossPaymentRepository.save(tossPayment);
		order.setTossPayment(tossPayment);

		return OrderRes.builder()
			.orderId(order.getOrderUuid())
			.customerKey(user.getSocialId())
			.email(user.getEmail())
			.username(user.getUsername())
			.build();
	}

	private Orders makeOrderEntity(Users user, String product, Long amount) {
		Orders order = Orders.builder()
			.users(user)
			.orderStatus(OrderStatus.ORDER_REQUEST)
			.orderUuid(UUID.randomUUID().toString())
			.product(product)
			.amount(amount)
			.build();

		return orderRepository.save(order);
	}

	/**
	 * 결제 승인 메서드
	 * @param confirmPaymentReq amount, orderId, paymentKey
	 */
	@Transactional
	public boolean confirm(String idempotencyKey, ConfirmPaymentReq confirmPaymentReq) {

		String paymentKey = confirmPaymentReq.getPaymentKey();
		String orderUuid = confirmPaymentReq.getOrderId();
		Long amount = confirmPaymentReq.getAmount();
		String cancelReason = "결제 정보 저장 실패";

		Orders order = getOrder(orderUuid);

		validateOrder(order, idempotencyKey, amount);

		ResponseEntity<TossPaymentRes> response = requestTossPaymentApprove(paymentKey, idempotencyKey, order, amount);
		String tossPaymentTraceId = response.getHeaders().get("X-TossPayments-Trace-Id").get(0);
		HttpStatus statusCode = (HttpStatus)response.getStatusCode();
		TossPaymentRes tossPaymentRes = response.getBody();

		if (statusCode == HttpStatus.OK) {
			try {
				// 주문 상태 업데이트 및 TossPayment 정보 저장
				order.updateOrderStatus(OrderStatus.ORDER_COMPLETE);
				updateTossPayment(order, tossPaymentRes, tossPaymentTraceId, null);

				// 포인트 증가 로직
				Users user = order.getUsers();
				user.addPayedPoint(amount);
				user.addPoint(amount);

				// 포인트 증가 내역 저장
				makePointHistory(user, PointHistoryType.CHARGE, amount);

				return true;
			} catch (Exception e) {
				// DB 저장 or 포인트 증가 실패 시 자동 결제 취소 요청
				String tempIdempotencyKey = UUID.randomUUID().toString();
				requestTossPaymentCancel(paymentKey, tempIdempotencyKey, order, cancelReason);
				order.updateOrderStatus(OrderStatus.ORDER_REFUND);
				updateTossPayment(order, tossPaymentRes, tossPaymentTraceId, cancelReason);

				return false;
			}
		} else {
			// 응답 코드가 200이 아닌 경우
			TossPaymentExceptionDto tossPaymentExceptionDto = payErrorService.makePaymentExceptionDto(statusCode,
				tossPaymentRes);

			throw new TossPaymentException(tossPaymentExceptionDto);
		}
	}

	private void validateOrder(Orders order, String idempotencyKey, Long amount) {
		if (!order.getOrderStatus().equals(OrderStatus.ORDER_REQUEST)) {
			throw new BusinessException(ResponseCode.BAD_REQUEST);
		}
		if (!order.getAmount().equals(amount)) {
			throw new BusinessException(ResponseCode.BAD_REQUEST);
		}

		if (!payRedisService.isIdempotent(idempotencyKey, order.getOrderUuid())) {
			throw new BusinessException(ResponseCode.BAD_REQUEST);
		}
	}

	private void updateTossPayment(Orders order, TossPaymentRes tossPaymentRes, String traceId, String cancelReason) {

		TossPayment tossPayment = order.getTossPayment();

		tossPayment.updateTossPaymentKey(tossPaymentRes.getPaymentKey());
		tossPayment.updateTossTraceId(traceId);
		tossPayment.updateTossPaymentStatus(tossPaymentRes.getStatus());
		tossPayment.updateTossPaymentMethod(tossPaymentRes.getMethod());
		tossPayment.updateTotalAmount(tossPaymentRes.getTotalAmount());
		tossPayment.updateRequestedAt(changeTime(tossPaymentRes.getRequestedAt()));
		tossPayment.updateApprovedAt(changeTime(tossPaymentRes.getApprovedAt()));

		if (cancelReason != null) {
			tossPayment.updateCancelReason(cancelReason);
		}
	}

	/**
	 * 토스 페이먼츠에 결제 승인 요청하는 메서드 (돈 빠져나가는 메서드)
	 * @param paymentKey
	 * @param order
	 * @param amount
	 * @return
	 */
	private ResponseEntity<TossPaymentRes> requestTossPaymentApprove(String paymentKey, String idempotencyKey,
		Orders order, Long amount) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = makeTossHeader(idempotencyKey);

		Map<String, Object> params = new HashMap<>();
		params.put("amount", amount);
		params.put("orderId", order.getOrderUuid());
		params.put("paymentKey", paymentKey);

		try {
			return restTemplate.postForEntity(
				TossPaymentConfig.TOSS_PAYMENT_BASEURL + "/confirm",
				new HttpEntity<>(params, headers),
				TossPaymentRes.class
			);
		} catch (Exception e) {
			throw new BusinessException(ResponseCode.PAYMENT_FAIL);
		}
	}

	private void makePointHistory(Users user, PointHistoryType pointHistoryType, Long amount) {
		PointHistory pointHistory = PointHistory.builder()
			.users(user)
			.pointHistoryType(pointHistoryType)
			.changeAmount(amount)
			.pickMe(user.getPickMe())
			.totalPoint(user.getPoint())
			.build();

		pointHistoryRepository.save(pointHistory);
	}

	/**
	 * 결제 전액 취소 메서드
	 * @param paymentKey
	 * @param order
	 * @param cancelReason
	 * @return
	 */
	private TossPaymentRes requestTossPaymentCancel(String paymentKey, String idempotencyKey, Orders order,
		String cancelReason) {

		if (!order.getOrderStatus().equals(OrderStatus.ORDER_COMPLETE)) {
			throw new BusinessException(ResponseCode.BAD_REQUEST);
		}

		boolean idempotent = payRedisService.isIdempotent(idempotencyKey, order.getOrderUuid());
		if (!idempotent) {
			throw new BusinessException(ResponseCode.BAD_REQUEST);
		}

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = makeTossHeader(idempotencyKey);
		String requestBody = String.format("{\"cancelReason\":\"%s\"}", cancelReason);

		try {
			ResponseEntity<TossPaymentRes> response = restTemplate.postForEntity(
				TossPaymentConfig.TOSS_PAYMENT_BASEURL + "/" + paymentKey + "/cancel",
				new HttpEntity<>(requestBody, headers),
				TossPaymentRes.class
			);
			return response.getBody();
		} catch (Exception e) {
			throw new BusinessException(ResponseCode.PAYMENT_FAIL);
		}
	}

	/**
	 * 부분 결제 취소 메서드
	 * @param paymentKey
	 * @param order
	 * @param cancelReason
	 * @param amount
	 * @return
	 */
	private TossPaymentRes requestTossPaymentCancel(String paymentKey, String idempotencyKey, Orders order,
		String cancelReason, Long amount) {

		if (!order.getOrderStatus().equals(OrderStatus.ORDER_COMPLETE)) {
			throw new BusinessException(ResponseCode.BAD_REQUEST);
		}

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = makeTossHeader(idempotencyKey);
		String requestBody = String.format("{\"cancelReason\":\"%s\", \"cancelAmount\":%d}", cancelReason, amount);

		try {
			ResponseEntity<TossPaymentRes> response = restTemplate.postForEntity(
				TossPaymentConfig.TOSS_PAYMENT_BASEURL + "/" + paymentKey + "/cancel",
				new HttpEntity<>(requestBody, headers),
				TossPaymentRes.class
			);
			return response.getBody();
		} catch (Exception e) {
			throw new BusinessException(ResponseCode.PAYMENT_FAIL);
		}
	}

	private HttpHeaders makeTossHeader() {
		HttpHeaders headers = new HttpHeaders();

		String encodedAuthKey = Base64.getEncoder()
			.encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

		headers.setBasicAuth(encodedAuthKey);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		return headers;
	}

	private HttpHeaders makeTossHeader(String idempotencyKey) {
		HttpHeaders headers = new HttpHeaders();

		String encodedAuthKey = Base64.getEncoder()
			.encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

		headers.setBasicAuth(encodedAuthKey);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set("Idempotency-Key", idempotencyKey);
		return headers;
	}

	private Orders getOrder(String orderUuid) {
		return orderRepository.findByOrderUuid(orderUuid)
			.orElseThrow(() -> new BusinessException(ResponseCode.BAD_REQUEST));
	}

	private LocalDateTime changeTime(String dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

		OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateTime, formatter);
		return offsetDateTime.toLocalDateTime();
	}
}
