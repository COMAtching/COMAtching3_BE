package comatching.comatching3.users.controller;

import comatching.comatching3.admin.dto.request.EmailVerifyReq;
import comatching.comatching3.admin.dto.response.EmailTokenRes;
import comatching.comatching3.users.dto.request.BuyPickMeReq;
import comatching.comatching3.users.dto.request.UserFeatureReq;
import comatching.comatching3.users.dto.request.UserRegisterReq;
import comatching.comatching3.users.dto.request.UserUpdateInfoReq;
import comatching.comatching3.users.dto.response.CurrentPointRes;
import comatching.comatching3.users.dto.response.PointRes;
import comatching.comatching3.users.dto.response.UserInfoRes;
import comatching.comatching3.users.dto.response.UsernamePointRes;
import comatching.comatching3.users.service.UserService;
import comatching.comatching3.util.Response;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/participations")
    public Response<Long> getParticipations() {
        Long result = userService.getParticipations();
        return Response.ok(result);
    }

    /**
     * 유저 회원가입
     */
    @PostMapping("/user/register")
    public Response<Void> userRegister(@RequestBody @Validated UserRegisterReq form) {
        userService.userRegister(form);
        return Response.ok();
    }

    /**
     * 유저 피처 입력
     *
     * @param form social 유저의 Feature
     * @return 처리 결과 반환
     */
    @PostMapping("/auth/social/api/user/info")
    public Response<Void> inputUserInfo(@RequestBody UserFeatureReq form, HttpServletRequest request) {
        userService.inputUserInfo(form, request);

        return Response.ok();
    }

    /**
     * 프로필 수정
     *
     * @param form
     */
    @PatchMapping("/auth/user/api/user/info")
    public Response<Void> updateUserInfo(@RequestBody UserUpdateInfoReq form) {
        userService.updateUserInfo(form);

        return Response.ok();
    }

    /**
     * contactId 중복확인
     */
    @GetMapping("/auth/allUser/api/check/{contactId}")
    public Response<Boolean> isContactIdDuplicated(@PathVariable String contactId) {
        return Response.ok(userService.isContactIdDuplicated(contactId));
    }

    @GetMapping("/auth/allUser/api/check-username/{username}")
    public Response<Boolean> isUsernameDuplicated(@PathVariable String username) {
        return Response.ok(userService.checkUsernameDuplicated(username));
    }

    /**
     * contactId 변경
     */
    // @Deprecated
    // @PatchMapping("/auth/user/api/user/info/{contactId}")
    // public Response<Void> updateContactId(@PathVariable String contactId) {
    //     userService.updateContactId(contactId);
    //     return Response.ok();
    // }

    @GetMapping("/auth/user/school-domain/{universityName}")
    public Response<String> getSchoolDomain(@PathVariable String universityName) {
        return Response.ok(userService.getSchoolDomain(universityName));
    }

    /**
     * 유저 학교 인증
     */
    @PostMapping("/auth/user/api/auth/school")
    @RateLimiter(name = "send-email")
    public Response<EmailTokenRes> userSchoolAuth(@RequestParam String schoolEmail) {
        String token = userService.userSchoolAuth(schoolEmail);

        return Response.ok(new EmailTokenRes(token));
    }

    /**
     * 입력한 코드가 맞는지 확인하는 메소드
     * 3분동안 최대 10번 시도 가능, 그 이후에는 만료
     *
     * @param request 토큰 값과 입력한 코드 번호를 이용해서 redis에 저장된 값과 비교
     * @return 인증 성공 시 ok, 실패 시 VAL-001
     */
    @PostMapping("/auth/user/api/auth/school/code")
    @RateLimiter(name = "verify-email")
    public Response<Boolean> verifyCode(@Validated @RequestBody EmailVerifyReq request) {
        boolean result = userService.verifyCode(request);
        return Response.ok(result);
    }

    /**
     * 메인 페이지 유저 정보 조회
     * 학교 인증 여부 및 학교 이메일도 추가
     *
     * @return 유저 정보
     */
    @GetMapping("/auth/user/api/info")
    public Response<UserInfoRes> getUserInfo() {
        UserInfoRes userInfo = userService.getUserInfo();
        return Response.ok(userInfo);
    }

    /**
     * 유저 닉네임 + 포인트 조회
     *
     * @return
     */
    @GetMapping("/auth/user/profile")
    public Response<UsernamePointRes> getProfile() {
        return Response.ok(userService.getProfile());
    }

    /**
     * 유저 포인트 조회
     *
     * @return 유저 포인트
     */
    @GetMapping("/auth/user/api/points")
    public Response<PointRes> getPoints() {
        PointRes points = userService.getPoints();
        return Response.ok(points);
    }

    /**
     * 유저 로그아웃
     * 전략 패턴 적용
     *
     * @return ok
     */
    // @GetMapping("/auth/allUser/api/logout")
    // public Response<Void> userLogout(HttpServletResponse response) throws IOException {
    //
    // 	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    // 	if (authentication == null || !authentication.isAuthenticated()) {
    // 		return Response.errorResponse(ResponseCode.ALREADY_LOGOUT);
    // 	}
    //
    // 	Users user = securityUtil.getCurrentUsersEntity();
    // 	if (user == null) {
    // 		return Response.errorResponse(ResponseCode.ALREADY_LOGOUT);
    // 	}
    //
    // 	String provider = user.getProvider();
    //
    // 	if (provider.equals(OAuth2Provider.KAKAO.getRegistrationId())) {
    // 		LogoutService logoutService = logoutServiceFactory.getLogoutService(provider);
    // 		logoutService.logout(user);
    // 	}
    //
    // 	// SecurityContext 비우기
    // 	SecurityContextHolder.clearContext();
    //
    // 	response.sendRedirect(REDIRECT_URL);
    //
    // 	return Response.ok();
    // }


    @GetMapping("/auth/user/api/currentPoint")
    public Response<CurrentPointRes> inquiryCurrentPoint() {
        CurrentPointRes res = userService.inquiryCurrentPoint();
        return Response.ok(res);
    }

    /**
     * 회원 탈퇴
     */
    @PatchMapping("/auth/user/api/remove")
    public Response<Void> removeUser(HttpServletRequest request, HttpServletResponse response) {
        userService.removeUser(request, response);
        return Response.ok();
    }
}
