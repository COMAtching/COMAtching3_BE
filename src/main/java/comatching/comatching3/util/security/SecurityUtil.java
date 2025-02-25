package comatching.comatching3.util.security;

import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.enums.AdminRole;
import comatching.comatching3.admin.repository.AdminRepository;
import comatching.comatching3.auth.details.CustomAdmin;
import comatching.comatching3.auth.details.CustomUser;
import comatching.comatching3.auth.dto.LoginDto;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

	private final UsersRepository usersRepository;
	private final AdminRepository adminRepository;

	/**
	 * @return 현재 로그인 한 유저의 UserDetails
	 */
	public static UserDetails getCurrentUserDetails() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			return (UserDetails)authentication.getPrincipal();
		}
		return null;
	}

	/**
	 * @return 현재 로그인 한 유저의 UUID
	 */
	public static Optional<String> getCurrentUserUUID() {
		UserDetails userDetails = getCurrentUserDetails();
		return Optional.ofNullable(userDetails).map(UserDetails::getUsername);
	}

	/**
	 * @return 현재 Users Entity 반환
	 */
	public Users getCurrentUsersEntity() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.getPrincipal() instanceof CustomUser customUser) {
			byte[] uuid = UUIDUtil.uuidStringToBytes(customUser.getUsername());

			return usersRepository.findUsersByUuid(uuid)
				.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
		}

		throw new BusinessException(ResponseCode.USER_NOT_FOUND);
	}

	public Admin getAdminFromContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.getPrincipal() instanceof CustomAdmin customAdmin) {
			String uuid = customAdmin.getUuid();

			return adminRepository.findByUuid(UUIDUtil.uuidStringToBytes(uuid))
				.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
		}

		throw new BusinessException(ResponseCode.USER_NOT_FOUND);
	}

	public void setNewUserSecurityContext(Object userObj, HttpServletRequest request) {
		Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

		UserDetails updatedUser = null;
		if (userObj instanceof Users) {
			Users user = (Users)userObj;
			LoginDto updatedLoginDto = LoginDto.builder()
				.uuid(UUIDUtil.bytesToHex(user.getUserAiFeature().getUuid()))
				.password(user.getPassword())
				.role("ROLE_USER")
				.build();

			updatedUser = new CustomUser(updatedLoginDto);
		} else {
			Admin admin = (Admin)userObj;
			LoginDto updatedLoginDto = LoginDto.builder()
				.uuid(UUIDUtil.bytesToHex(admin.getUuid()))
				.password(admin.getPassword())
				.role("ROLE_ADMIN")
				.build();

			updatedUser = new CustomAdmin(updatedLoginDto);
		}

		Authentication newAuth = new UsernamePasswordAuthenticationToken(
			updatedUser,
			updatedUser.getPassword(),
			updatedUser.getAuthorities()
		);

		SecurityContextHolder.getContext().setAuthentication(newAuth);
		request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

	}
}
