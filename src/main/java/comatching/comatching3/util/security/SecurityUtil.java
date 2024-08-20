package comatching.comatching3.util.security;

import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UsersRepository usersRepository;
    private final AdminRepository adminRepository;

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

    /**
     *
     * @return 현재 Users Entity 반환
     */
    public Users getCurrentUsersEntity(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails =  (UserDetails) authentication.getPrincipal();
            byte[] uuid = UUIDUtil.uuidStringToBytes(userDetails.getUsername());

            return usersRepository.findUsersByUuid(uuid)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
        }

        throw new BusinessException(ResponseCode.USER_NOT_FOUND);
    }

    public Admin getAdminFromContext() {
        String adminUuid = getCurrentUserUUID()
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        return adminRepository.findByUuid(UUIDUtil.uuidStringToBytes(adminUuid))
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }
}
