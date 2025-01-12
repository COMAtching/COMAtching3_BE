package comatching.comatching3.users.auth.oauth2.handler;

import comatching.comatching3.admin.dto.response.TokenRes;
import comatching.comatching3.users.auth.jwt.JwtUtil;
import comatching.comatching3.users.auth.oauth2.provider.CustomUser;
import comatching.comatching3.users.auth.oauth2.service.TokenService;
import comatching.comatching3.users.auth.refresh_token.service.RefreshTokenService;
import comatching.comatching3.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final TokenService tokenService;
    private final CookieUtil cookieUtil;

    @Value("${redirect-url.frontend}")
     private String REDIRECT_URL;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUser customUser = getOAuth2UserPrincipal(authentication);

        if (customUser == null) {
            log.error("Failed to get PrincipalUser from authentication");
            response.sendRedirect("/oauth2-error");
        }

        TokenRes tokenRes = tokenService.makeTokenRes(customUser.getUuid(), customUser.getRole());
        response.addHeader("Set-Cookie", cookieUtil.setAccessResponseCookie(tokenRes.getAccessToken()).toString());
        response.addHeader("Set-Cookie", cookieUtil.setRefreshResponseCookie(tokenRes.getRefreshToken()).toString());
        refreshTokenService.saveRefreshTokenInRedis(customUser.getUuid(), tokenRes.getRefreshToken());

        response.sendRedirect(REDIRECT_URL);
    }

    private CustomUser getOAuth2UserPrincipal(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUser) {
            return (CustomUser)principal;
        }
        return null;
    }

}
