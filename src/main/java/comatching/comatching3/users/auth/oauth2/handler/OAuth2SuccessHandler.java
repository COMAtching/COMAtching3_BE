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
        String uuid = user.getName();
        String role = user.getRole();

        String accessToken = jwtUtil.generateAccessToken(uuid, role);
        String refreshToken = refreshTokenService.getRefreshToken(uuid);

        if (refreshToken == null) {
            refreshToken = jwtUtil.generateRefreshToken(uuid, role);
            refreshTokenService.saveRefreshToken(uuid, refreshToken);
        }

        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("Refresh-Token", refreshToken);

//        response.sendRedirect(REDIRECT_URI + accessToken);
    }

}
