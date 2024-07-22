package comatching.comatching3.users.auth.oauth2.handler;

import comatching.comatching3.users.auth.jwt.JwtUtil;
import comatching.comatching3.users.auth.oauth2.dto.CustomOAuth2User;
import comatching.comatching3.users.auth.refresh_token.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    // private String REDIRECT_URI = "http://localhost:8080/social/login/success?token=";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        String socialId = user.getName();

        // 사용자 권한 추출
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority(); // 첫 번째 권한 추출

        String accessToken = jwtUtil.generateAccessToken(socialId, role);
        String refreshToken = refreshTokenService.getRefreshToken(socialId);

        if (refreshToken == null) {
            refreshToken = jwtUtil.generateRefreshToken(socialId, role);
            refreshTokenService.saveRefreshToken(socialId, refreshToken);
        }

        response.addHeader("Authorization", accessToken);
        response.addHeader("Refresh-Token", refreshToken);

//        response.sendRedirect(REDIRECT_URI + accessToken);
    }

}
