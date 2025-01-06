package comatching.comatching3.users.auth.jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import comatching.comatching3.admin.auth.AdminDto;
import comatching.comatching3.admin.auth.CustomAdmin;
import comatching.comatching3.admin.enums.AdminRole;
import comatching.comatching3.users.auth.oauth2.dto.UserDto;
import comatching.comatching3.users.auth.oauth2.provider.CustomUser;
import comatching.comatching3.users.auth.refresh_token.service.RefreshTokenService;
import comatching.comatching3.util.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private static final List<String> WHITELIST = List.of(
		"/login",
		"/api/match",
		"/charge-monitor",
		"/admin",
		"/auth/refresh"
	);
	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		log.info("요청 URL = {}", requestURI);
		if (WHITELIST.stream().anyMatch(requestURI::startsWith)) {
			filterChain.doFilter(request, response);
			return;
		}

		String encryptedAccessToken = getTokenFromCookies(request, "accessToken");

		try {
			if (encryptedAccessToken != null) {
				// 토큰 복호화
				String accessToken = jwtUtil.decryptToken(encryptedAccessToken);

				if (!jwtUtil.isExpired(accessToken)) {
					// 복호화된 토큰 유효성 검증
					setAuthentication(accessToken);
					filterChain.doFilter(request, response);
					return;
				}
			}
		} catch (ExpiredJwtException e) {
			log.info("엑세스 토큰 만료");
			throw new JwtException("TOKEN_EXPIRED");
		} catch (SignatureException e) {
			log.info("엑세스 토큰 무결성 오류");
			throw new JwtException("TOKEN_INVALID");
		} catch (JwtException e) {
			log.info("엑세스 토큰 오류");
			throw new JwtException("TOKEN_INVALID");
		}

		filterChain.doFilter(request, response);
	}

	private void setAuthentication(String accessToken) {
		String uuid = jwtUtil.getUUID(accessToken);
		String role = jwtUtil.getRole(accessToken);

		if (AdminRole.isValidRole(role)) {
			AdminDto adminDto = AdminDto.builder()
				.uuid(uuid)
				.role(role)
				.build();

			CustomAdmin adminUser = new CustomAdmin(adminDto);
			Authentication authToken = new UsernamePasswordAuthenticationToken(adminUser, null, adminUser.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authToken);

		} else {
			UserDto userDto = UserDto.builder()
				.uuid(uuid)
				.role(role)
				.build();

			CustomUser customUser = new CustomUser(userDto);

			Authentication authToken = new UsernamePasswordAuthenticationToken(
				customUser,
				null,
				customUser.getAuthorities()
			);

			SecurityContextHolder.getContext().setAuthentication(authToken);
		}

	}

	private String getTokenFromCookies(HttpServletRequest request, String cookieName) {
		if (request.getCookies() == null) {
			return null;
		}

		return Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(cookieName))
			.findFirst()
			.map(Cookie::getValue)
			.orElse(null);
	}
}
