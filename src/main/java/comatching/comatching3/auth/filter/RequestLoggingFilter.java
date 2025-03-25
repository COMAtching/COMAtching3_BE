package comatching.comatching3.auth.filter;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import comatching.comatching3.auth.details.CustomAdmin;
import comatching.comatching3.auth.details.CustomUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 요청 URI와 세션 ID 가져오기
		String requestURI = request.getRequestURI();
		HttpSession session = request.getSession(false);
		String sessionId = (session != null) ? session.getId() : "no session";

		// 현재 SecurityContext의 인증 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String authInfo = (authentication != null) ? authentication.toString() : "no auth";
		log.info("Incoming request: {} | Session ID: {} | Authentication: {}", requestURI, sessionId, authInfo);

		// principal 타입에 따라 로그 출력
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof CustomUser) {
				log.info("User UUID = {}", ((CustomUser) principal).getUuid());
			} else if (principal instanceof CustomAdmin) {
				log.info("Admin UUID = {}", ((CustomAdmin) principal).getUuid());
			} else if (principal instanceof UserDetails) {
				log.info("UserDetails username = {}", ((UserDetails) principal).getUsername());
			} else if (principal instanceof OAuth2User) {
				OAuth2User oauth2User = (OAuth2User) principal;
				log.info("OAuth2User attributes = {}", oauth2User.getAttributes());
			} else {
				log.info("Principal = {}", principal);
			}
		}

		// 다음 필터로 요청 전달
		filterChain.doFilter(request, response);
	}
}
