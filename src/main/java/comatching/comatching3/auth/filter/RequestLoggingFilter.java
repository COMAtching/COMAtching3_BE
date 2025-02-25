package comatching.comatching3.auth.filter;

import java.io.IOException;

import comatching.comatching3.auth.details.CustomAdmin;
import comatching.comatching3.auth.details.CustomUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		// 요청 URI 가져오기
		String requestURI = request.getRequestURI();

		// 세션이 있으면 세션 ID, 없으면 "no session"
		HttpSession session = request.getSession(false);
		String sessionId = (session != null) ? session.getId() : "no session";

		// 현재 SecurityContext의 인증 정보
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String authInfo = (authentication != null) ? authentication.toString() : "no auth";

		// 로그 출력
		log.info("Incoming request: {} | Session ID: {} | Authentication: {}", requestURI, sessionId, authInfo);

		if (authentication != null) {
			UserDetails principal = (UserDetails)authentication.getPrincipal();
			if (principal instanceof CustomUser){
				log.info("User UUID = {}", ((CustomUser)principal).getUuid());
			} else {
				log.info("Admin UUID = {}", ((CustomAdmin)principal).getUuid());
			}
		}


		// 다음 필터로 요청 전달
		filterChain.doFilter(request, response);
	}
}
