package comatching.comatching3.admin.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import comatching.comatching3.admin.dto.request.AccountIdCheckReq;
import comatching.comatching3.admin.dto.request.AdminInfoUpdateReq;
import comatching.comatching3.admin.dto.request.AdminRegisterReq;
import comatching.comatching3.admin.dto.request.EmailVerifyReq;
import comatching.comatching3.admin.dto.response.AdminInfoRes;
import comatching.comatching3.admin.dto.response.AfterVerifyEmailRes;
import comatching.comatching3.admin.dto.response.EmailTokenRes;
import comatching.comatching3.admin.dto.response.OperatorRes;
import comatching.comatching3.admin.service.AdminService;
import comatching.comatching3.admin.service.OperatorService;
import comatching.comatching3.admin.service.UniversityService;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.service.BlackListService;
import comatching.comatching3.util.CookieUtil;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;
	private final UniversityService universityService;
	private final OperatorService operatorService;
	private final BlackListService blackListService;
	private final CookieUtil cookieUtil;

	/**
	 * 관리자 회원가입
	 * 비속어 필터 적용
	 */
	@PostMapping("/admin/register")
	public Response<Void> adminRegister(@Validated @RequestBody AdminRegisterReq form) {

		boolean isDuplicated = operatorService.checkEmailDuplicate(form.getSchoolEmail());
		boolean checkEmailDomain = universityService.checkEmailDomain(form.getSchoolEmail(), form.getUniversity());
		boolean checkBlackList = blackListService.checkBlackListByEmail(form.getSchoolEmail());

		if (isDuplicated || !checkEmailDomain) {
			throw new BusinessException(ResponseCode.ARGUMENT_NOT_VALID);
		}

		if (checkBlackList) {
			throw new BusinessException(ResponseCode.BLACK_USER);
		}

		adminService.adminRegister(form);
		return Response.ok();
	}

	/**
	 * 관리자 회원가입 시 아이디 중복확인 메소드
	 * @param accountIdCheckReq 아이디
	 * @return true, false
	 */
	@PostMapping("/admin/register/check-id")
	public Response<Boolean> isAccountDuplicated(@Validated @RequestBody AccountIdCheckReq accountIdCheckReq) {
		Boolean result = adminService.isAccountDuplicated(accountIdCheckReq.getAccountId());
		return Response.ok(result);
	}

	/**
	 * 관리자 학교 메일 인증 메소드
	 * 하루에 5번만 전송 가능
	 * @return success
	 */
	@PostMapping("/auth/semi/email/code")
	@RateLimiter(name = "send-email")
	public Response<EmailTokenRes> sendVerificationCode() {
		String token = adminService.sendVerifyEmail();
		return Response.ok(new EmailTokenRes(token));
	}

	/**
	 * 입력한 코드가 맞는지 확인하는 메소드
	 * 3분동안 최대 10번 시도 가능, 그 이후에는 만료
	 * @param request 토큰 값과 입력한 코드 번호를 이용해서 redis에 저장된 값과 비교
	 * @return 인증 성공 시 ok, 실패 시 VAL-001
	 */
	@PostMapping("/auth/semi/email/verify/code")
	@RateLimiter(name = "verify-email")
	public Response<Void> verifyCode(@Validated @RequestBody EmailVerifyReq request, HttpServletResponse response) {
		AfterVerifyEmailRes result = adminService.verifyCode(request);

		if (result.getSuccess()) {
			ResponseCookie accessCookie = cookieUtil.setAccessResponseCookie(result.getAccessToken());
			ResponseCookie refreshCookie = cookieUtil.setRefreshResponseCookie(result.getRefreshToken());

			response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
			response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
			return Response.ok();
		} else {
			return Response.errorResponse(ResponseCode.ARGUMENT_NOT_VALID);
		}
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
	 * 오퍼레이터 거절 메서드
	 * 거절된 오퍼레이터 계정은 삭제됨
	 * @param operatorId uuid
	 * @return ok
	 */
	@DeleteMapping("/auth/admin/pending-operator/{operatorId}")
	public Response<Void> denyOperator(@PathVariable String operatorId) {
		adminService.denyOperator(operatorId);

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
	 * admin 정보 수정 메소드 (닉네임만 바꿀 수 있음)
	 * @param request 닉네임
	 * @return 성공시 ok, 실패 시 400
	 */
	@PatchMapping("/auth/operator/info")
	public Response<Void> updateAdminInfo(@RequestBody AdminInfoUpdateReq request) {
		adminService.updateAdminInfo(request);

		return Response.ok();
	}
}
