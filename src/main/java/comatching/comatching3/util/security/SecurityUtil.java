package comatching.comatching3.util.security;

import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.repository.AdminRepository;
import comatching.comatching3.users.auth.jwt.JwtUtil;
import comatching.comatching3.users.auth.oauth2.dto.CustomOAuth2User;
import comatching.comatching3.users.auth.oauth2.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final JwtUtil jwtUtil;

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

    public void setAuthentication(String accessToken) {
        String uuid = jwtUtil.getUUID(accessToken);
        String role = jwtUtil.getRole(accessToken);

        UserDto userDto = new UserDto();
        userDto.setUuid(uuid);
        userDto.setRole(role);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDto);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    /**
     *
     * @return 현재 Users Entity 반환
     */
    public Users getCurrentUsersEntity(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
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
