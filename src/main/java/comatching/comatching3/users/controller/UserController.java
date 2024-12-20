package comatching.comatching3.users.controller;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import comatching.comatching3.admin.dto.response.TokenRes;
import comatching.comatching3.users.auth.oauth2.provider.OAuth2Provider;
import comatching.comatching3.users.auth.oauth2.service.LogoutService;
import comatching.comatching3.users.auth.oauth2.service.LogoutServiceFactory;
import comatching.comatching3.users.auth.refresh_token.service.RefreshTokenService;
import comatching.comatching3.users.dto.BuyPickMeReq;
import comatching.comatching3.users.dto.CurrentPointRes;
import comatching.comatching3.users.dto.UserFeatureReq;
import comatching.comatching3.users.dto.UserInfoRes;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.service.UserService;
import comatching.comatching3.util.CookieUtil;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final CookieUtil cookieUtil;
	private final LogoutServiceFactory logoutServiceFactory;
	private final SecurityUtil securityUtil;
	private final RefreshTokenService refreshTokenService;

	@GetMapping("/api/participations")
	public Response<Long> getParticipations() {
		Long result = userService.getParticipations();
		return Response.ok(result);
	}

	/**
	 * 유저 피처 입력
	 * @param form social 유저의 Feature
	 * @return 처리 결과 반환
	 */
	@PostMapping("/auth/social/api/user/info")
	public Response<Void> inputUserInfo(@RequestBody UserFeatureReq form,
		HttpServletResponse response) {
		TokenRes tokenRes = userService.inputUserInfo(form);

		response.addHeader("Set-Cookie", cookieUtil.setAccessResponseCookie(tokenRes.getAccessToken()).toString());
		response.addHeader("Set-Cookie", cookieUtil.setRefreshResponseCookie(tokenRes.getRefreshToken()).toString());

		return Response.ok();
	}

	/**
	 * contactId 변경
	 * @param contactId 소셜 ID
	 * @return 200
	 */
	@PatchMapping("/auth/user/api/user/info/{contactId}")
	public Response<Void> updateContactId(@PathVariable String contactId) {
		userService.updateContactId(contactId);
		return Response.ok();
	}

	/**
	 * 메인 페이지 유저 정보 조회
	 * @return 유저 정보
	 */
	@GetMapping("/auth/user/api/info")
	public Response<UserInfoRes> getUserInfo() {
		UserInfoRes userInfo = userService.getUserInfo();
		return Response.ok(userInfo);
	}

	/**
	 * 유저 포인트 조회
	 * @return 유저 포인트
	 */
	@GetMapping("/auth/user/api/points")
	public Response<String> getPoints() {
		Integer points = userService.getPoints();
		return Response.ok("point : " + points);
	}

	/**
	 * 유저 로그아웃
	 * 전략 패턴 적용
	 * @return ok
	 */
	@GetMapping("/auth/user/api/logout")
	public Response<Void> userLogout(HttpServletResponse response) throws IOException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return Response.errorResponse(ResponseCode.ALREADY_LOGOUT);
		}

		Users user = securityUtil.getCurrentUsersEntity();
		if (user == null) {
			return Response.errorResponse(ResponseCode.ALREADY_LOGOUT);
		}

		String provider = user.getProvider();

		// 전략 패턴 적용했지만 카카오 말곤 로그아웃 api를 제공하지 않음.. ㅠ
		if (provider.equals(OAuth2Provider.KAKAO.getRegistrationId())) {
			LogoutService logoutService = logoutServiceFactory.getLogoutService(provider);
			logoutService.logout(user);
		}

		// 쿠키 삭제 처리
		response.addHeader("Set-Cookie", cookieUtil.deleteAccessResponseCookie().toString());
		response.addHeader("Set-Cookie", cookieUtil.deleteRefreshResponseCookie().toString());

		// 리프레시 토큰 삭제
		String uuid = UUIDUtil.bytesToHex(user.getUserAiFeature().getUuid());
		refreshTokenService.deleteRefreshToken(uuid);

		// SecurityContext 비우기
		SecurityContextHolder.clearContext();

		response.sendRedirect("/main-page");

		return Response.ok();
	}

	@PostMapping("/auth/user/api/pickme")
	public Response<Void> buyPickMe(@RequestBody BuyPickMeReq request) {
		userService.buyPickMe(request);
		return Response.ok();
	}

	@GetMapping("/auth/user/api/currentPoint")
	public Response<CurrentPointRes> inquiryCurrentPoint() {
		CurrentPointRes res = userService.inquiryCurrentPoint();
		return Response.ok(res);
	}

	@GetMapping("/auth/user/api/event/pickMe")
	public Response<Void> requestEventPickMe() {
		userService.requestEventPickMe();
		return Response.ok();
	}

	@GetMapping("/auth/user/api/event/no-pickMe")
	public Response<Void> notRequestEventPickMe() {
		userService.notRequestEventPickMe();
		return Response.ok();
	}

	/**
	 * 더이상 안뽑히기 (사실상 탈퇴)
	 * @return 200
	 */
	@GetMapping("/auth/user/api/stop-pickMe")
	public Response<Void> stopPickMe() {
		userService.stopPickMe();
		return Response.ok();
	}

	/**
	 * 다시 뽑히기
	 * pickMe가 있어야 csv에 추가됨
	 * @return 200
	 */
	@GetMapping("/auth/user/api/restart-pickMe")
	public Response<Void> restartAccount() {
		userService.restartPickMe();
		return Response.ok();
	}
}
