package comatching.comatching3.auth.filter;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import comatching.comatching3.admin.enums.AdminRole;
import comatching.comatching3.auth.details.CustomAdmin;
import comatching.comatching3.auth.service.CustomDetailsService;
import comatching.comatching3.users.service.BlackListService;
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
	private final BlackListService blackListService;

	public AdminAuthenticationFilter(AuthenticationManager authenticationManager,
		CustomDetailsService customDetailsService,
		BlackListService blackListService) {
		super(authenticationManager, customDetailsService);
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

			setDetails(request, authToken);
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

		SecurityContextHolder.getContext().setAuthentication(authResult);

		request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
		// response.sendRedirect("https://comatching.site/adminpage/mypage");

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		String jsonResponse = "{\"redirectUrl\":\"https://comatching.site/adminpage/mypage\"}";
		response.getWriter().write(jsonResponse);

	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
		throws IOException, ServletException {
		sendAuthenticationFailed(response);
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
