package comatching.comatching3.auth.filter;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import comatching.comatching3.auth.details.CustomAdmin;
import comatching.comatching3.auth.service.CustomDetailsService;
import comatching.comatching3.auth.filter.AbstractAuthenticationFilter;
import comatching.comatching3.users.auth.jwt.JwtUtil;
import comatching.comatching3.auth.details.CustomUser;
import comatching.comatching3.users.auth.refresh_token.service.RefreshTokenService;
import comatching.comatching3.users.service.BlackListService;
import comatching.comatching3.util.CookieUtil;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdminAuthenticationFilter extends AbstractAuthenticationFilter {

	private static final String LOGIN_URL = "/admin/login";
	private final JwtUtil jwtUtil;
	private final CookieUtil cookieUtil;
	private final RefreshTokenService refreshTokenService;
	private final BlackListService blackListService;

	public AdminAuthenticationFilter(AuthenticationManager authenticationManager,
		CustomDetailsService customDetailsService,
		JwtUtil jwtUtil,
		CookieUtil cookieUtil,
		RefreshTokenService refreshTokenService,
		BlackListService blackListService) {
		super(authenticationManager, customDetailsService);
		this.jwtUtil = jwtUtil;
		this.cookieUtil = cookieUtil;
		this.refreshTokenService = refreshTokenService;
		this.blackListService = blackListService;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException {
		try {
			Map<String, String> creds = parseRequest(request);
			String accountId = getUsernameKey(creds);
			String password = getPasswordKey(creds);
			String username = "ADMIN:" + accountId;

			UsernamePasswordAuthenticationToken authToken =
				new UsernamePasswordAuthenticationToken(username, password);

			return authenticationManager.authenticate(authToken);
		} catch (IOException e) {
			throw new AuthenticationServiceException("로그인 정보를 읽을 수 없습니다.", e);
		}
	}

	@Override
	protected String getLoginUrl() {
		return LOGIN_URL;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authResult) throws IOException,
		ServletException {

		CustomAdmin customAdmin = (CustomAdmin)authResult.getPrincipal();

		if (checkBlackListForAdmin(customAdmin, response)) {
			// 블랙리스트에 포함된 경우 차단 후 로직 종료
			return;
		}

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
		response.getWriter().write(Response.ok(ResponseCode.SUCCESS).convertToJson());
		response.getWriter().flush();
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {
		// 인증 실패 시 처리 로직
		authenticationFailed(response);
	}

	private static void authenticationFailed(HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.getWriter().write(Response.errorResponse(ResponseCode.INVALID_LOGIN).convertToJson());
		response.getWriter().flush();
	}

	private boolean checkBlackListForAdmin(CustomAdmin admin, HttpServletResponse response) throws IOException {
		if (isInBlackListed(admin.getUuid())) {
			log.info("블랙된 관리자");
			blockAccess(response);
			return true;
		}
		return false;
	}

	private void blockAccess(HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.sendRedirect("/");
		response.getWriter().write(Response.errorResponse(ResponseCode.BLACK_USER).convertToJson());
		response.getWriter().flush();
	}

	private boolean isInBlackListed(String uuid) {
		byte[] byteUuid = UUIDUtil.uuidStringToBytes(uuid);
		return blackListService.checkBlackListByUuid(byteUuid);
	}

}
