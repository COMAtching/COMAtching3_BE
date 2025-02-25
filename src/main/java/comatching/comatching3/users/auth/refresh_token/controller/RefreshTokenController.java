// package comatching.comatching3.users.auth.refresh_token.controller;
//
// import java.util.Arrays;
//
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.ResponseCookie;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import comatching.comatching3.users.auth.jwt.JwtUtil;
// import comatching.comatching3.users.auth.refresh_token.service.RefreshTokenService;
// import comatching.comatching3.util.CookieUtil;
// import comatching.comatching3.util.Response;
// import comatching.comatching3.util.ResponseCode;
// import jakarta.servlet.http.Cookie;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;
//
// @RestController
// @RequiredArgsConstructor
// public class RefreshTokenController {
//
// 	private final JwtUtil jwtUtil;
// 	private final RefreshTokenService refreshTokenService;
// 	private final CookieUtil cookieUtil;
//
// 	@GetMapping("/auth/refresh")
// 	public Response<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
// 		String encryptedRefreshToken = Arrays.stream(request.getCookies())
// 			.filter(cookie -> "refreshToken".equals(cookie.getName()))
// 			.findFirst()
// 			.map(Cookie::getValue)
// 			.orElse(null);
//
// 		if (encryptedRefreshToken == null) {
// 			return Response.errorResponse(ResponseCode.JWT_ERROR);
// 		}
//
// 		try {
// 			String refreshToken = jwtUtil.decryptToken(encryptedRefreshToken);
// 			String uuid = jwtUtil.getUUID(refreshToken);
// 			String role = jwtUtil.getRole(refreshToken);
//
// 			String storeEncryptRefreshToken = refreshTokenService.getRefreshToken(uuid);
// 			String storedRefreshToken = jwtUtil.decryptToken(storeEncryptRefreshToken);
//
// 			if (storedRefreshToken == null) {
// 				return Response.errorResponse(ResponseCode.JWT_ERROR);
// 			}
//
// 			// 리프레시 토큰 일치 여부 및 만료 여부 검증
// 			if (jwtUtil.isExpired(refreshToken)) {
// 				return Response.errorResponse(ResponseCode.JWT_ERROR);
// 			}
//
// 			if (!refreshToken.equals(storedRefreshToken)) {
// 				return Response.errorResponse(ResponseCode.JWT_ERROR);
// 			}
//
// 			String newAccessToken = jwtUtil.generateAccessToken(uuid, role);
// 			String newRefreshToken = jwtUtil.generateRefreshToken(uuid, role);
//
// 			// 기존 쿠키 삭제
// 			ResponseCookie deleteAccessCookie = cookieUtil.deleteAccessResponseCookie();
// 			ResponseCookie deleteRefreshCookie = cookieUtil.deleteRefreshResponseCookie();
// 			response.addHeader(HttpHeaders.SET_COOKIE, deleteAccessCookie.toString());
// 			response.addHeader(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString());
//
// 			// 새 쿠키 설정
// 			ResponseCookie accessCookie = cookieUtil.setAccessResponseCookie(newAccessToken);
// 			ResponseCookie refreshCookie = cookieUtil.setRefreshResponseCookie(newRefreshToken);
// 			refreshTokenService.saveRefreshTokenInRedis(uuid, newRefreshToken);
// 			response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
// 			response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
//
// 			return Response.ok();
// 		} catch (Exception e) {
// 			return Response.errorResponse(ResponseCode.JWT_ERROR);
// 		}
// 	}
// }