package comatching.comatching3.util;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

	public ResponseCookie setRefreshResponseCookie(String refreshToken) {
		ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
			.httpOnly(true)
			.secure(false)
			.path("/auth/refresh")
			// .domain(".comatching.site")
			.maxAge(24 * 60 * 60) // 1일
			.sameSite("Strict")
			.build();
		return refreshCookie;
	}

	public ResponseCookie setAccessResponseCookie(String accessToken) {
		ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
			.httpOnly(true)
			.secure(false) // HTTPS 환경에서만 전송하려면 true
			.path("/")
			// .domain(".comatching.site")
			.maxAge(60 * 60) // 1시간
			.sameSite("Strict")
			.build();
		return accessCookie;
	}

	public ResponseCookie deleteAccessResponseCookie() {
		return ResponseCookie.from("accessToken", "")
			.httpOnly(true)
			.secure(false)
			.path("/")
			//.domain(".comatching.site")
			.maxAge(0) // 쿠키 삭제
			.sameSite("Strict")
			.build();
	}

	public ResponseCookie deleteRefreshResponseCookie() {
		return ResponseCookie.from("refreshToken", "")
			.httpOnly(true)
			.secure(false)
			// .domain(".comatching.site")
			.path("/auth/refresh")
			.maxAge(0) // 쿠키 삭제
			.sameSite("Strict")
			.build();
	}
}
