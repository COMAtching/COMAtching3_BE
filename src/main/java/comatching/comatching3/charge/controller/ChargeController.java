package comatching.comatching3.charge.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import comatching.comatching3.charge.dto.request.ChargeApprovalReq;
import comatching.comatching3.charge.dto.request.ChargeCancelReq;
import comatching.comatching3.charge.dto.request.ChargeReq;
import comatching.comatching3.charge.service.ChargeService;
import comatching.comatching3.util.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChargeController {

    private final ChargeService chargeService;

    @PostMapping("/auth/user/api/charge")
    public Response<Void> createChargeRequest(@RequestBody ChargeReq chargeReq) {
        chargeService.createChargeRequest(chargeReq);
        return Response.ok();
    }

    @MessageMapping("/approveCharge")
    public void handleChargeApproval(ChargeApprovalReq approvalReq) {
        log.info("[handleChargeApproval] = arrived");
        chargeService.createApprovalRequest(approvalReq);
    }

    @MessageMapping("/cancelCharge")
    public void handleCancelCharge(ChargeCancelReq chargeCancelReq) {
        chargeService.cancelChargeRequest(chargeCancelReq);
    }
}
