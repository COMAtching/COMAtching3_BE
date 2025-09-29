package comatching.comatching3.history.controller;

import java.util.List;

import comatching.comatching3.history.dto.res.PointHistoryRes;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import comatching.comatching3.history.dto.res.MatchHistoryRes;
import comatching.comatching3.history.service.HistoryService;
import comatching.comatching3.pay.dto.res.PayHistoryRes;
import comatching.comatching3.pay.service.PayHistoryService;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HistoryController {
	private final HistoryService historyService;
	private final PayHistoryService payHistoryService;
	private final SecurityUtil securityUtil;

	@GetMapping("/auth/user/api/history/matching")
	public Response<List<MatchHistoryRes>> inquiryMatchHistory(){
		return Response.ok(historyService.inquiryMatchHistory());
	}

	@GetMapping("/auth/user/api/history/picked-me")
	public Response<List<MatchHistoryRes>> getMatchHistoryPickedMe() {
		return Response.ok(historyService.getMatchHistoryPickedMe());
	}

	/**
	 * 관리자의 유저 포인트 내역 조회
	 * @param uuid
	 * @return
	 */
	@GetMapping("/auth/operator/api/history/point/{uuid}")
	public Response<List<PointHistoryRes>> getAllPointHistory(@PathVariable String uuid) {
		return Response.ok(historyService.getAllPointHistory(UUIDUtil.uuidStringToBytes(uuid)));
	}

	/**
	 * 관리자의 유저 결제 내역 조회
	 * @param uuid
	 * @return
	 */
	@GetMapping("/auth/operator/api/history/payment/{uuid}")
	public Response<List<PayHistoryRes>> getAllPaymentHistory(@PathVariable String uuid) {
		return Response.ok(payHistoryService.getPaymentHistory(UUIDUtil.uuidStringToBytes(uuid)));
	}

	@GetMapping("/auth/user/api/history/point")
	public Response<List<PointHistoryRes>> getAllPointHistory() {
		Users user = securityUtil.getCurrentUsersEntity();

		return Response.ok(historyService.getAllPointHistory(user.getUserAiFeature().getUuid()));
	}

}

