package comatching.comatching3.pay.service;

import java.util.List;

import org.springframework.stereotype.Service;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.pay.dto.res.PayHistoryRes;
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

		// 테스트용
		// Users user = usersRepository.findBySocialId("3490175542")
		// 	.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

		return getPayHistoryResList(user);
	}

	/**
	 * 관리자의 유저 결제 내역 조회
	 * @param uuid
	 * @return
	 */
	public List<PayHistoryRes> getPaymentHistory(byte[] uuid) {
		Users user = usersRepository.findUsersByUuid(uuid)
			.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

		return getPayHistoryResList(user);
	}

	private List<PayHistoryRes> getPayHistoryResList(Users user) {
		return orderRepository.findAllByUsers(user).stream()
			.map(order -> PayHistoryRes.builder()
				.productName(order.getProduct())
				.orderStatus(order.getOrderStatus())
				.requestAt(order.getTossPayment().getRequestedAt().toString())
				.approvedAt(order.getTossPayment().getApprovedAt().toString())
				.cancelReason(order.getTossPayment().getCancelReason())
				.amount(order.getAmount())
				.tossPaymentMethod(order.getTossPayment().getTossPaymentMethod())
				.build())
			.toList();
	}
}
