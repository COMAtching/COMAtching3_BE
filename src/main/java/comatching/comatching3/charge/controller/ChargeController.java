package comatching.comatching3.charge.controller;

import comatching.comatching3.charge.dto.request.ChargeApprovalReq;
import comatching.comatching3.charge.dto.request.ChargeReq;
import comatching.comatching3.charge.service.ChargeService;
import comatching.comatching3.util.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

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
        System.out.println("approvalReq.getUserId() = " + approvalReq.getUserId());
        System.out.println("approvalReq.getAmount() = " + approvalReq.getAmount());
        System.out.println("approvalReq.getApprovalTime() = " + approvalReq.getApprovalTime());
        chargeService.createApprovalRequest(approvalReq);
    }
}
