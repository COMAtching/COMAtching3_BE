package comatching.comatching3.pay.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.pay.dto.res.PayHistoryRes;
import comatching.comatching3.pay.enums.OrderStatus;
import comatching.comatching3.pay.repository.OrderRepository;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayHistoryService {

	private final SecurityUtil securityUtil;
	private final OrderRepository orderRepository;
	private final UsersRepository usersRepository;

	/**
	 * 사용자의 결제 내역 조회
	 * @return
	 */
	public List<PayHistoryRes> getPayHistory() {
		Users user = securityUtil.getCurrentUsersEntity();

		return getSuccessPayHistoryResList(user);
	}

	/**
	 * 관리자의 유저 결제 내역 조회 (성공, 실패, 만료 등 모두 조회)
	 * @param uuid
	 * @return
	 */
	public List<PayHistoryRes> getPaymentHistory(byte[] uuid) {
		Users user = usersRepository.findUsersByUuid(uuid)
			.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

		return getAllPayHistoryResList(user);
	}

	private List<PayHistoryRes> getSuccessPayHistoryResList(Users user) {
		List<PayHistoryRes> list = new ArrayList<>(orderRepository.findCompleteByUsers(user).stream()
			.map(order -> PayHistoryRes.builder()
				.productName(order.getProduct())
				.orderStatus(order.getOrderStatus())
				.requestAt(order.getTossPayment().getRequestedAt().toString())
				.approvedAt(order.getTossPayment().getApprovedAt().toString())
				.cancelReason(order.getTossPayment().getCancelReason())
				.price(order.getAmount())
				.point(order.getPoint())
				.orderId(order.getOrderUuid().substring(0, 13))
				.tossPaymentMethod(order.getTossPayment().getTossPaymentMethod())
				.build())
			.toList());

		Collections.reverse(list);
		return list;
	}

	private List<PayHistoryRes> getAllPayHistoryResList(Users user) {
		return orderRepository.findAllByUsers(user).stream()
			.map(order -> PayHistoryRes.builder()
				.productName(order.getProduct())
				.orderStatus(order.getOrderStatus())
				.requestAt(order.getTossPayment().getRequestedAt().toString())
				.approvedAt(order.getTossPayment().getApprovedAt().toString())
				.cancelReason(order.getTossPayment().getCancelReason().equals("Not Canceled") ? "정상 결제" : "취소되거나 만료된 주문입니다.")
				.price(order.getAmount())
				.point(order.getPoint())
				.tossPaymentMethod(order.getTossPayment().getTossPaymentMethod())
				.build())
			.toList();
	}
}
