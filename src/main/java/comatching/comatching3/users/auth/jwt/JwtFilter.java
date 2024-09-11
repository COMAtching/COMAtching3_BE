package comatching.comatching3.users.auth.jwt;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.auth.refresh_token.service.RefreshTokenService;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.security.SecurityUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final SecurityUtil securityUtil;

    private static final List<String> WHITELIST = List.of(
            "/login",
            "/api/match",
            "/charge-monitor",
            "/admin"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
//        log.info("요청 URL = {}", requestURI);
        if (WHITELIST.stream().anyMatch(requestURI::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        setSecurityHeaders(response);

        String accessToken = getAccessToken(request);

        try {
            if (accessToken != null && !jwtUtil.isExpired(accessToken)) {
//                log.info("엑세스 토큰 유효");
                securityUtil.setAuthentication(accessToken);
                filterChain.doFilter(request, response);
                return;
            }
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ResponseCode.TOKEN_NOT_AVAILABLE);
//            log.info("엑세스 토큰 만료");
        } catch (SignatureException e) {
            log.info("엑세스 토큰 무결성 오류");
            throw new JwtException("TOKEN_INVALID");
        } catch (JwtException e) {
            log.info("엑세스 토큰 오류");
//            response.sendRedirect("/login");
            throw new JwtException("TOKEN_INVALID");
        }

        String refreshToken = getRefreshToken(request);

        try {
            if (refreshToken != null && !jwtUtil.isExpired(refreshToken)) {
//                log.info("헤더 리프레시 토큰 유효");
                String socialId = jwtUtil.getUUID(refreshToken);
                String role = jwtUtil.getRole(refreshToken);
                String redisRefreshToken = refreshTokenService.getRefreshToken(socialId);

                if (redisRefreshToken != null && redisRefreshToken.equals(refreshToken)) {
//                    log.info("레디스 리프레시 토큰까지 유효, 엑세스 토큰 재발급");
                    String newAccessToken = jwtUtil.generateAccessToken(socialId, role);
//                    log.info("새 엑세스 토큰 출력 = {}", newAccessToken);

//                    log.info("리프레시 토큰을 사용했으므로 재발급");
                    String newRefreshToken = jwtUtil.generateRefreshToken(socialId, role);
//                    log.info("새 리프레시 토큰 출력 = {}", newRefreshToken);
                    refreshTokenService.saveRefreshToken(socialId, newRefreshToken);

                    response.setHeader("Authorization", "Bearer " + newAccessToken);
                    response.setHeader("Refresh-Token", newRefreshToken);
                    securityUtil.setAuthentication(newAccessToken);
                    Users user = securityUtil.getCurrentUsersEntity();
//                    log.info("새로 등록한 유저 정보 출력(필터 안) " + user.getUsername() + "권한 " + user.getRole());
                } else {
//                    log.info("레디스와 리프레시 토큰 다름");
//                    response.sendRedirect("/login");
                    throw new JwtException("TOKEN_INVALID");
                }
            }
        } catch (ExpiredJwtException e) {
//            log.info("리프레시 토큰 만료");
//            response.sendRedirect("/login");
            throw new JwtException("TOKEN_EXPIRED");
        } catch (SignatureException e) {
//            log.info("리프레시 토큰 무결성 오류");
            throw new JwtException("TOKEN_INVALID");
        } catch (JwtException e) {
//            log.info("리프레시 토큰 오류");
            throw new JwtException("TOKEN_INVALID");
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            log.info("엑세스 토큰 출력 = {}", bearerToken.substring(7));
            return bearerToken.substring(7);
        }

        return null;
    }

    private String getRefreshToken(HttpServletRequest request) {
//        log.info("리프레시 토큰 출력 = {}", request.getHeader("Refresh-Token"));
        return request.getHeader("Refresh-Token");
    }

    private void setSecurityHeaders(HttpServletResponse response) {
        response.setHeader("X-Content-Type-Options", "nosniff"); //브라우저가 MIME 타입을 스니핑하지 못하도록 설정
//        response.setHeader("X-Frame-Options", "DENY"); //페이지가 iframe 또는 프레임에 삽입되지 않도록 설정하여 Clickjacking 공격을 방지
//        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains"); //모든 연결이 HTTPS를 통해 이루어지도록 강제
//        response.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self' https://trusted-cdn.com");
    }

}
