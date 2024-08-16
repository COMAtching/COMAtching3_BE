package comatching.comatching3.admin.controller;

import comatching.comatching3.admin.dto.request.EmailVerifyReq;
import comatching.comatching3.admin.dto.request.SchoolEmailReq;
import comatching.comatching3.admin.dto.response.EmailTokenRes;
import comatching.comatching3.admin.service.OperatorService;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OperatorController {

    private final OperatorService operatorService;

    /**
     * 관리자 학교 메일 인증 메소드
     * @param schoolEmailReq 인증용 이메일
     * @return success
     */
    @PostMapping("/auth/semi/email/code")
    public Response<EmailTokenRes> sendVerificationCode(@RequestBody SchoolEmailReq schoolEmailReq) {

        Boolean isDuplicated = operatorService.checkEmailDuplicate(schoolEmailReq.getSchoolEmail());
        Boolean checkEmailDomain = operatorService.checkEmailDomain(schoolEmailReq.getSchoolEmail());

        if (isDuplicated || !checkEmailDomain) {
            return Response.errorResponse(ResponseCode.ARGUMENT_NOT_VALID);
        }

        String token = operatorService.sendVerifyEmail(schoolEmailReq);
        return Response.ok(new EmailTokenRes(token));
    }

    /**
     * 입력한 코드가 맞는지 확인하는 메소드
     * @param request 토큰 값과 입력한 코드 번호를 이용해서 redis에 저장된 값과 비교
     * @return 인증 성공 시 ok, 실패 시 VAL-001
     */
    @PostMapping("/auth/semi/email/verify/code")
    public Response<Void> verifyCode(@RequestBody EmailVerifyReq request) {

        Boolean result = operatorService.verifyCode(request);

        if (result) {
            return Response.ok();
        } else {
            return Response.errorResponse(ResponseCode.ARGUMENT_NOT_VALID);
        }
    }

    @PostMapping("/admin/email/account/id")
    public Response<Void> sendFindIdEmail(@RequestBody SchoolEmailReq schoolEmailReq) {
        operatorService.sendFindIdEmail(schoolEmailReq.getSchoolEmail());

        return Response.ok();
    }
}
