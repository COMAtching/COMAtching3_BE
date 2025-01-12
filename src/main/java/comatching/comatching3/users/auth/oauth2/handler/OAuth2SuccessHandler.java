package comatching.comatching3.users.auth.oauth2.handler;

import comatching.comatching3.admin.dto.response.TokenRes;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.auth.oauth2.provider.CustomUser;
import comatching.comatching3.users.auth.oauth2.service.TokenService;
import comatching.comatching3.users.auth.refresh_token.service.RefreshTokenService;
import comatching.comatching3.users.service.BlackListService;
import comatching.comatching3.util.CookieUtil;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
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

    private final BlackListService blackListService;
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
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
        }

        if (checkBlackListForUser(customUser, response)) {
            log.info("블랙된 유저");
            return;
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

    private boolean checkBlackListForUser(CustomUser user, HttpServletResponse response) throws IOException {
        if (isInBlackListed(user.getUuid())) {
            blockAccess(response);
            return true;
        }
        return false;
    }

    private void blockAccess(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.sendRedirect("/login");
        response.getWriter().write(Response.errorResponse(ResponseCode.BLACK_USER).convertToJson());
        response.getWriter().flush();
    }

    private boolean isInBlackListed(String uuid) {
        byte[] byteUuid = UUIDUtil.uuidStringToBytes(uuid);
        return blackListService.checkBlackListByUuid(byteUuid);
    }
}
