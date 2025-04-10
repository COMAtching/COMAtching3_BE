package comatching.comatching3.admin.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import comatching.comatching3.admin.dto.request.BlackUserReq;
import comatching.comatching3.admin.dto.request.ChangeUserPointReq;
import comatching.comatching3.admin.dto.request.EmailReq;
import comatching.comatching3.admin.dto.request.ResetPasswordReq;
import comatching.comatching3.admin.dto.request.SendResetPasswordEmailReq;
import comatching.comatching3.admin.dto.response.UserBasicInfoRes;
import comatching.comatching3.admin.service.OperatorService;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.history.dto.res.GenderRes;
import comatching.comatching3.users.dto.response.BlackListRes;
import comatching.comatching3.users.service.BlackListService;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OperatorController {

	private final BlackListService blackListService;
	private final OperatorService operatorService;

	/**
	 * 관리자 아이디 찾기 메소드
	 * 하루 5번만 가능
	 * @param emailReq 학교 이메일
	 * @return GEN-000
	 */
	@PostMapping("/admin/email/account/id")
	@RateLimiter(name = "send-email")
	public Response<Void> sendFindIdEmail(@Validated @RequestBody EmailReq emailReq) {
		operatorService.sendFindIdEmail(emailReq.getEmail());

		return Response.ok();
	}

	/**
	 * 관리자 비밀번호 재설정 이메일 요청 메소드
	 * 하루 5번만 가능
	 * @param sendResetPasswordEmailReq 아이디, 학교 이메일
	 * @return GEN-000
	 */
	@PostMapping("/admin/email/account/password")
	@RateLimiter(name = "send-email")
	public Response<Void> sendResetPasswordEmail(
		@Validated @RequestBody SendResetPasswordEmailReq sendResetPasswordEmailReq) {
		operatorService.sendResetPasswordEmail(sendResetPasswordEmailReq);

		return Response.ok();
	}

	/**
	 * 관리자 비밀번호 재설정 메소드
	 * @param resetPasswordReq 토큰, 비밀번호, 확인 비밀번호
	 * @return GEN-000
	 */
	@PostMapping("/admin/password")
	public Response<Void> resetPassword(HttpServletResponse response,
		@Validated @ModelAttribute ResetPasswordReq resetPasswordReq) throws IOException {
		operatorService.resetPassword(resetPasswordReq);

		response.sendRedirect("https://comatching.site:8080/admin");
		return Response.ok();
	}

	/**
	 * 블랙 추가
	 */
	@PostMapping("/auth/operator/black/user")
	public Response<Void> blackUser(@RequestBody BlackUserReq blackUserReq) {
		operatorService.blackUser(blackUserReq);

		return Response.ok();
	}

	/**
	 * 블랙 해제
	 */
	@DeleteMapping("/auth/operator/black/user/{uuid}")
	public Response<Void> unBlackUser(@PathVariable String uuid) {
		operatorService.unBlackUser(uuid);

		return Response.ok();
	}

	/**
	 * 블랙리스트 조회
	 */
	@GetMapping("/auth/operator/black-list")
	public Response<List<BlackListRes>> getAllBlackList() {
		return Response.ok(blackListService.getAllBlackList());
	}

	/**
	 * 유저 기본정보 전체 조회
	 */
	@GetMapping("/auth/operator/user-list")
	public Response<PagedModel<UserBasicInfoRes>> getUserBasicInfoList(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "50") int size,
		@RequestParam(required = false) String searchType,
		@RequestParam(required = false) String keyword,
		PagedResourcesAssembler assembler) {

		Page<UserBasicInfoRes> result = operatorService.getUserBasicInfoList(searchType, keyword, page, size);
		return Response.ok(assembler.toModel(result));
	}

	/**
	 * 유저 기본정보 조회 (검색)
	 * @param uuid
	 */
	@GetMapping("/auth/operator/user")
	public Response<UserBasicInfoRes> findUserInfo(
		@RequestParam(name = "uuid") String uuid) {

		if (uuid == null) {
			throw new BusinessException(ResponseCode.BAD_REQUEST);
		}

		UserBasicInfoRes result = operatorService.getUserBasicInfoByUuid(uuid);
		return Response.ok(result);
	}



	// 회원 포인트 수동 조작
	@PatchMapping("/auth/operator/api/point")
	public Response<Void> changeUserPoint(@RequestBody ChangeUserPointReq req) {
		operatorService.changeUserPoint(req.getUuid(), req.getPoint(), req.getReason());
		return Response.ok();
	}

	// 전체 회원 남/녀 수 확인
	@GetMapping("/auth/operator/api/gender")
	public Response<GenderRes> getGenderRatio() {
		return Response.ok(operatorService.getGenderRatio());
	}

	// todo: 회원 경고

}
