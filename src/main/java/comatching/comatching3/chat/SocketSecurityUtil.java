package comatching.comatching3.chat;

import comatching.comatching3.auth.details.CustomUser;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocketSecurityUtil {

    public static Users getCurrentUsersEntity(WebSocketSession session, UsersRepository usersRepository) {
        HttpSession httpSession = (HttpSession) session.getAttributes().get("HTTP_SESSION");

        if (httpSession == null) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }

        Object context = httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
        if (context instanceof SecurityContext securityContext) {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUser customUser) {
                byte[] uuid = UUIDUtil.uuidStringToBytes(customUser.getUsername());
                return usersRepository.findUsersByUuid(uuid)
                        .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
            }
        }

        throw new BusinessException(ResponseCode.USER_NOT_FOUND);
    }

    /**
     * @param session WebSocketSession
     * @return 현재 WebSocket 세션의 로그인 유저 정보 (UserDetails)
     */
    public static UserDetails getCurrentUserDetails(WebSocketSession session) {
        HttpSession httpSession = (HttpSession) session.getAttributes()
                .get(HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME);

        if (httpSession == null) return null;

        Object context = httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
        if (context instanceof SecurityContext securityContext) {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
                return userDetails;
            }
        }

        return null;
    }
}
