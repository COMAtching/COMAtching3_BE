package comatching.comatching3.charge.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import comatching.comatching3.charge.dto.request.TempChargeApprovalReq;
import comatching.comatching3.charge.dto.response.TempChargeRes;
import comatching.comatching3.charge.service.TempChargeService;
import comatching.comatching3.pay.dto.req.OrderReq;
import comatching.comatching3.pay.dto.res.PayHistoryRes;
import comatching.comatching3.util.Response;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TempChargeController {

	private final TempChargeService tempChargeService;

	@PostMapping("/auth/user/tempay/charge")
	public Response<Void> requestCharge(@RequestBody OrderReq request) {
		tempChargeService.requestCharge(request);

		return Response.ok();
	}

	@PostMapping("/auth/operator/tempay/approval")
	public Response<Void> approvalChargeRequest(@RequestBody TempChargeApprovalReq request) {
		tempChargeService.approvalChargeRequest(request);

		return Response.ok();
	}

	@DeleteMapping("/auth/operator/tempay/refund")
	public Response<Void> refundChargeRequest(@RequestBody TempChargeApprovalReq request) {
		tempChargeService.refundChargeRequest(request);

		return Response.ok();
	}

	@GetMapping("/auth/operator/tempay/charge-list")
	public Response<List<TempChargeRes>> getChargeRequests() {
		List<TempChargeRes> chargeRequests = tempChargeService.getChargeRequests();

		return Response.ok(chargeRequests);
	}
}
