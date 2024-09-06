package comatching.comatching3.users.auth.oauth2.handler;

import comatching.comatching3.users.auth.jwt.JwtUtil;
import comatching.comatching3.users.auth.oauth2.dto.CustomOAuth2User;
import comatching.comatching3.users.auth.refresh_token.service.RefreshTokenService;
import comatching.comatching3.users.enums.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Value("${redirect-url.frontend}")
     private String REDIRECT_URL;

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

        String userRole = "USER";
        if (role.equals(Role.SOCIAL.getRoleName())) {
            userRole = "SOCIAL";
        }

        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("Refresh-Token", refreshToken);
        response.sendRedirect(REDIRECT_URL + "/?accessToken=" + accessToken + "&refreshToken=" + refreshToken + "&userRole=" + userRole);
    }

}
