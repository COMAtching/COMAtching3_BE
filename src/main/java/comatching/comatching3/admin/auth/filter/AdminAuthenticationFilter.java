package comatching.comatching3.admin.auth.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import comatching.comatching3.admin.auth.CustomAdmin;
import comatching.comatching3.admin.auth.service.AdminUserDetailsService;
import comatching.comatching3.users.auth.jwt.JwtUtil;
import comatching.comatching3.users.auth.refresh_token.service.RefreshTokenService;
import comatching.comatching3.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AdminAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final AdminUserDetailsService adminUserDetailsService;
	private final JwtUtil jwtUtil;
	private final CookieUtil cookieUtil;
	private final RefreshTokenService refreshTokenService;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {
		try {
			// JSON 형식으로 전송된 관리자 로그인 정보 파싱
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> creds = mapper.readValue(request.getInputStream(), Map.class);
			String username = creds.get("accountId");
			String password = creds.get("password");

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

			return authenticationManager.authenticate(authToken);
		} catch (IOException e) {
			throw new AuthenticationServiceException("로그인 정보를 읽을 수 없습니다.", e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authResult) throws IOException,
		ServletException {
		// 인증 성공 시 JWT 토큰 생성 및 응답
		CustomAdmin customAdmin = (CustomAdmin)authResult.getPrincipal();
		String accessToken = jwtUtil.generateAccessToken(customAdmin.getUuid(), customAdmin.getRole());
		String refreshToken = jwtUtil.generateRefreshToken(customAdmin.getUuid(), customAdmin.getRole());
		refreshTokenService.saveRefreshTokenInRedis(customAdmin.getUuid(), refreshToken);

		ResponseCookie accessCookie = cookieUtil.setAccessResponseCookie(accessToken);
		ResponseCookie refreshCookie = cookieUtil.setRefreshResponseCookie(refreshToken);

		response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

		SecurityContextHolder.getContext().setAuthentication(authResult);
		// 응답을 종료하여 다음 필터로 전달되지 않도록 함
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		Map<String, String> successMap = new HashMap<>();
		successMap.put("message", "로그인에 성공했습니다.");
		new ObjectMapper().writeValue(response.getOutputStream(), successMap);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {
		// 인증 실패 시 처리 로직
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put("error", "인증에 실패했습니다.");
		new ObjectMapper().writeValue(response.getOutputStream(), errorMap);
	}


}
