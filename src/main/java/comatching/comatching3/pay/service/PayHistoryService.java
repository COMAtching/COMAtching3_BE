package comatching.comatching3.pay.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import comatching.comatching3.pay.dto.res.PayHistoryRes;
import comatching.comatching3.pay.entity.Orders;
import comatching.comatching3.pay.repository.OrderRepository;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayHistoryService {

	private final SecurityUtil securityUtil;
	private final OrderRepository orderRepository;

	public List<PayHistoryRes> getPayHistory() {
		Users user = securityUtil.getCurrentUsersEntity();

		return orderRepository.findAllByUsers(user).stream()
			.map(order -> PayHistoryRes.builder()
				.productName(order.getProduct())
				.orderStatus(order.getOrderStatus())
				.requestAt(order.getTossPayment().getRequestedAt().toString())
				.approvedAt(order.getTossPayment().getApprovedAt().toString())
				.cancelReason(order.getTossPayment().getCancelReason())
				.tossPaymentMethod(order.getTossPayment().getTossPaymentMethod())
				.build())
			.toList();
	}
}
