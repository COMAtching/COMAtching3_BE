package comatching.comatching3.users.controller;

import comatching.comatching3.admin.dto.response.TokenRes;
import comatching.comatching3.users.dto.BuyPickMeReq;
import comatching.comatching3.users.dto.UserFeatureReq;
import comatching.comatching3.users.dto.UserInfoRes;
import comatching.comatching3.users.service.UserService;
import comatching.comatching3.util.Response;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * 유저 피처 입력
     * @param form social 유저의 Feature
     * @return 처리 결과 반환
     */
    @PostMapping("/auth/social/api/user/info")
    public Response<Void> inputUserInfo(@RequestBody UserFeatureReq form,
                                        HttpServletResponse response) {
        TokenRes tokens = userService.inputUserInfo(form);

        response.addHeader("Authorization", "Bearer " + tokens.getAccessToken());
        response.addHeader("Refresh-Token", tokens.getRefreshToken());
        return Response.ok();
    }

    /**
     * 메인 페이지 유저 정보 조회
     * @return 유저 정보
     */
    @GetMapping("/auth/user/api/info")
    public Response<UserInfoRes> getUserInfo() {
        log.info("컨트롤러 도착");
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

    @PostMapping("/auth/user/api/pickme")
    public Response<Void> buyPickMe(@RequestBody BuyPickMeReq request) {
        userService.buyPickMe(request);
        return Response.ok();
    }
}
