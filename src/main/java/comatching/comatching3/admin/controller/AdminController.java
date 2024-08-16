package comatching.comatching3.admin.controller;

import comatching.comatching3.admin.dto.request.*;
import comatching.comatching3.admin.dto.response.AdminInfoRes;
import comatching.comatching3.admin.dto.response.OperatorRes;
import comatching.comatching3.admin.dto.response.TokenRes;
import comatching.comatching3.admin.service.AdminService;
import comatching.comatching3.util.Response;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/admin/register")
    public Response<Void> adminRegister(@RequestBody AdminRegisterReq form) {
        adminService.adminRegister(form);
        return Response.ok();
    }

    @PostMapping("/admin/login")
    public Response<Void> adminLogin(@RequestBody AdminLoginReq form,
                           HttpServletResponse response) {

        TokenRes tokens = adminService.adminLogin(form);

        response.addHeader("Authorization", "Bearer " + tokens.getAccessToken());
        response.addHeader("Refresh-Token", tokens.getRefreshToken());

        return Response.ok();
    }

    /**
     * 승인 대기중인 오퍼레이터 조회 메소드
     * @return 승인 대기중인 오퍼레이터 목록 정보
     */
    @GetMapping("/auth/admin/pending-operator")
    public Response<List<OperatorRes>> getPendingOperators() {
        List<OperatorRes> pendingOperators = adminService.getPendingOperators();

        return Response.ok(pendingOperators);
    }

    /**
     * 오퍼레이터 승인 메소드
     * @param operatorId operator uuid
     * @return GEN-000
     */
    @PatchMapping("/auth/admin/pending-operator/{operatorId}")
    public Response<Void> accessOperator(@PathVariable String operatorId) {
        adminService.accessOperator(operatorId);

        return Response.ok();
    }


    /**
     * admin 정보 조회 메소드
     * @return admin 정보 반환
     */
    @GetMapping("/auth/operator/info")
    public Response<AdminInfoRes> getAdminInfo() {
        AdminInfoRes adminInfo = adminService.getAdminInfo();

        return Response.ok(adminInfo);
    }

    /**
     * admin 정보 수정 메소드
     * @param request 수정할 항목의 정보
     * @return 성공시 ok, 실패 시 400
     */
    @PatchMapping("/auth/operator/info")
    public Response<Void> updateAdminInfo(@RequestBody AdminInfoUpdateReq request) {
        adminService.updateAdminInfo(request);

        return Response.ok();
    }
}
