package comatching.comatching3.util.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtil {

    /**
     *
     * @return 현재 로그인 한 유저의 UserDetails
     */
    public static UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
    }

    /**
     *
     * @return 현재 로그인 한 유저의 UUID
     */
    public static Optional<String> getCurrentUserUUID() {
        UserDetails userDetails = getCurrentUserDetails();
        return Optional.ofNullable(userDetails).map(UserDetails::getUsername);
    }

    /**
     *
     * @return 현재 로그인 한 유저의 권한
     */
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !authentication.getAuthorities().isEmpty()) {
            return authentication
                    .getAuthorities()
                    .iterator()
                    .next()
                    .getAuthority();
        }
        return null;
    }
}
