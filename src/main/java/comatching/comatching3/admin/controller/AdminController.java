package comatching.comatching3.admin.controller;

import comatching.comatching3.admin.dto.request.*;
import comatching.comatching3.admin.dto.response.AdminInfoRes;
import comatching.comatching3.admin.dto.response.EmailTokenRes;
import comatching.comatching3.admin.dto.response.TokenRes;
import comatching.comatching3.admin.exception.AccountIdDuplicatedException;
import comatching.comatching3.admin.exception.UniversityNotExistException;
import comatching.comatching3.admin.service.AdminService;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/admin/register")
    public Response<Void> adminRegister(@RequestBody AdminRegisterReq form) {
        try {
            adminService.adminRegister(form);
        } catch (UniversityNotExistException e) {
            return Response.errorResponse(ResponseCode.ARGUMENT_NOT_VALID);
        } catch (AccountIdDuplicatedException e) {
            return Response.errorResponse(ResponseCode.ACCOUNT_ID_DUPLICATED);
        }
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
     * 관리자 학교 메일 인증 메소드
     * @param schoolEmailReq 인증용 이메일
     * @return success
     */
    @PostMapping("/auth/semi/email/code")
    public Response<EmailTokenRes> sendVerificationCode(@RequestBody SchoolEmailReq schoolEmailReq) {

        Boolean isDuplicated = adminService.checkEmailDuplicate(schoolEmailReq.getSchoolEmail());
        Boolean checkEmailDomain = adminService.checkEmailDomain(schoolEmailReq.getSchoolEmail());

        if (isDuplicated || !checkEmailDomain) {
            return Response.errorResponse(ResponseCode.ARGUMENT_NOT_VALID);
        }

        String token = adminService.sendVerifyEmail(schoolEmailReq);
        return Response.ok(new EmailTokenRes(token));
    }

    /**
     * 입력한 코드가 맞는지 확인하는 메소드
     * @param request 토큰 값과 입력한 코드 번호를 이용해서 redis에 저장된 값과 비교
     * @return 인증 성공 시 ok, 실패 시 VAL-001
     */
    @PostMapping("/auth/semi/email/verify/code")
    public Response<Void> verifyCode(@RequestBody EmailVerifyReq request) {

        Boolean result = adminService.verifyCode(request);

        if (result) {
            return Response.ok();
        } else {
            return Response.errorResponse(ResponseCode.ARGUMENT_NOT_VALID);
        }
    }

    /**
     * admin 정보 조회 메소드
     * @return admin 정보 반환
     */
    @GetMapping("/auth/admin/info")
    public Response<AdminInfoRes> getAdminInfo() {
        AdminInfoRes adminInfo = adminService.getAdminInfo();

        return Response.ok(adminInfo);
    }

    /**
     * admin 정보 수정 메소드
     * @param request 수정할 항목의 정보
     * @return 성공시 ok, 실패 시 400
     */
    @PatchMapping("/auth/admin/info")
    public Response<Void> updateAdminInfo(@RequestBody AdminInfoUpdateReq request) {
        adminService.updateAdminInfo(request);

        return Response.ok();
    }
}
