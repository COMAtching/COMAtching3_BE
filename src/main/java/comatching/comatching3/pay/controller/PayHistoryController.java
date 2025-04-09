package comatching.comatching3.pay.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import comatching.comatching3.charge.service.TempChargeService;
import comatching.comatching3.pay.dto.res.PayHistoryRes;
import comatching.comatching3.pay.service.PayHistoryService;
import comatching.comatching3.util.Response;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PayHistoryController {

	private final PayHistoryService payHistoryService;
	private final TempChargeService tempChargeService;

	// 토스 페이먼츠
	/*@GetMapping("/history")
	public Response<List<PayHistoryRes>> getPayHistoryList() {
		return Response.ok(payHistoryService.getPayHistory());
	}*/

	// 자체 결제
	@GetMapping("/history")
	public Response<List<PayHistoryRes>> getPayHistoryList() {
		return Response.ok(tempChargeService.getUserChargeHistoryTemp());
	}
}
