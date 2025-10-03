package comatching.comatching3.charge.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import comatching.comatching3.charge.dto.request.TempChargeApprovalReq;
import comatching.comatching3.charge.dto.response.TempChargeRes;
import comatching.comatching3.charge.entity.ChargeRequest;
import comatching.comatching3.charge.repository.ChargeRequestRepository;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.pay.dto.req.OrderReq;
import comatching.comatching3.pay.dto.res.PayHistoryRes;
import comatching.comatching3.pay.enums.OrderStatus;
import comatching.comatching3.setting.service.SystemSettingService;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TempChargeService {

	private final SecurityUtil securityUtil;
	private final ChargeRequestRepository chargeRequestRepository;
	private final SystemSettingService systemSettingService;

	@Transactional
	public void requestCharge(OrderReq request) {
		Users user = securityUtil.getCurrentUsersEntity();

		Optional<ChargeRequest> chargeRequestOpt = chargeRequestRepository.findByUsersAndOrderStatus(user,
			OrderStatus.ORDER_REQUEST);

		if (chargeRequestOpt.isPresent()) {
			throw new BusinessException(ResponseCode.ALREADY_REQUEST_CHARGE);
		}

		// int dailyChargeLimit = 30000;
		// if (user.getPayedPoint() > dailyChargeLimit) {
		// 	throw new BusinessException(ResponseCode.ENOUGH_DAILY_CHARGE);
		// }

		ChargeRequest chargeRequest = ChargeRequest.builder()
			.users(user)
			.productName(request.getProductName())
			.price(request.getAmount())
			.point(request.getPoint())
			.cancelReason("결제 대기")
			.orderStatus(OrderStatus.ORDER_REQUEST)
			.realName(request.getRealName())
			.build();

		user.setRealName(request.getRealName());

		chargeRequestRepository.save(chargeRequest);
	}

	@Transactional
	public void approvalChargeRequest(TempChargeApprovalReq request) {
		ChargeRequest chargeRequest = chargeRequestRepository.findByOrderId(request.getOrderId())
			.orElseThrow(() -> new BusinessException(ResponseCode.BAD_REQUEST));

		Users user = chargeRequest.getUsers();

		user.addPoint(chargeRequest.getPoint());
		user.addPayedPoint(chargeRequest.getPoint());

		chargeRequest.setApprovedAt(LocalDateTime.now());
		chargeRequest.setOrderStatus(OrderStatus.ORDER_COMPLETE);
		chargeRequest.setCancelReason("결제 성공");
	}


	@Transactional
	public void refundChargeRequest(TempChargeApprovalReq request) {
		ChargeRequest chargeRequest = chargeRequestRepository.findByOrderId(request.getOrderId())
			.orElseThrow(() -> new BusinessException(ResponseCode.BAD_REQUEST));

		chargeRequest.setApprovedAt(LocalDateTime.now());
		chargeRequest.setOrderStatus(OrderStatus.ORDER_REFUND);
		chargeRequest.setCancelReason("관리자에 의해 취소됨");
	}

	public List<TempChargeRes> getChargeRequests() {
		List<ChargeRequest> chargeRequests = chargeRequestRepository.findAllByOrderStatusOrderByRequestAtDesc(OrderStatus.ORDER_REQUEST);

		return chargeRequests.stream().map(
			chargeRequest -> TempChargeRes.builder()
				.productName(chargeRequest.getProductName())
				.price(chargeRequest.getPrice())
				.point(chargeRequest.getPoint())
				.username(chargeRequest.getUsers().getUsername())
				.orderId(chargeRequest.getOrderId())
				.requestAt(chargeRequest.getRequestAt().toString())
				.realName(chargeRequest.getRealName())
				.contact(chargeRequest.getUsers().getContactId())
				.build()
		).collect(Collectors.toList());
	}

	public List<PayHistoryRes> getUserChargeHistoryTemp() {
		Users user = securityUtil.getCurrentUsersEntity();

		List<ChargeRequest> histories = chargeRequestRepository.findAllByUsersOrderByCreatedAtAsc(user);

		return histories.stream().map(
			history -> PayHistoryRes.builder()
				.orderId(history.getOrderId())
				.point(history.getPoint())
				.price(history.getPrice())
				.requestAt(history.getRequestAt().toString())
				.approvedAt(history.getApprovedAt() != null ? history.getApprovedAt().toString() : null)
				.cancelReason(history.getCancelReason())
				.productName(history.getProductName())
				.tossPaymentMethod(null)
				.orderStatus(history.getOrderStatus())
				.build()
		).collect(Collectors.toList());
	}

	public List<PayHistoryRes> getAdminChargeHistoryTemp(Users user) {

		List<ChargeRequest> histories = chargeRequestRepository.findAllByUsersOrderByCreatedAtAsc(user);

		return histories.stream().map(
			history -> PayHistoryRes.builder()
				.orderId(history.getOrderId())
				.point(history.getPoint())
				.price(history.getPrice())
				.requestAt(history.getRequestAt().toString())
				.approvedAt(history.getApprovedAt() != null ? history.getApprovedAt().toString() : null)
				.cancelReason(history.getCancelReason())
				.productName(history.getProductName())
				.tossPaymentMethod(null)
				.orderStatus(history.getOrderStatus())
				.build()
		).collect(Collectors.toList());
	}

	@Transactional
	public void make1000() {
		Users user = securityUtil.getCurrentUsersEntity();

		Long point = user.getPoint();

		if (point >= 1000 || point == 0) {
			throw new BusinessException(ResponseCode.OVER_1000);
		} else if (user.isMake1000()) {
			throw new BusinessException(ResponseCode.ALREADY_USE);
		}else if (!systemSettingService.isBalanceButtonEnabled()) {
			throw new BusinessException(ResponseCode.BUTTON_NOT_ACTIVE);
		}

		user.addPoint(1000 - point);
		user.setMake1000(true);
	}

}
