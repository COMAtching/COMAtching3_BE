package comatching.comatching3.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.repository.AdminRepository;
import comatching.comatching3.auth.details.CustomAdmin;
import comatching.comatching3.auth.details.CustomUser;
import comatching.comatching3.auth.dto.LoginDto;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.dto.AnonymousUser;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomDetailsService implements UserDetailsService {

	private final AdminRepository adminRepository;
	private final UsersRepository usersRepository;

	@Override
	public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {

		if (accountId.startsWith("ADMIN:")) {
			String realId = accountId.substring("ADMIN:".length());
			Admin admin = adminRepository.findByAccountId(realId)
				.orElseThrow(() -> new UsernameNotFoundException("관리자를 찾을 수 없습니다."));

			LoginDto adminDto = LoginDto.builder()
				.accountId(admin.getAccountId())
				.password(admin.getPassword())
				.role(admin.getAdminRole().getRoleName())
				.uuid(UUIDUtil.bytesToHex(admin.getUuid()))
				.build();

			return new CustomAdmin(adminDto);
		} else if (accountId.startsWith("USER:")) {
			String realId = accountId.substring("USER:".length());

			if (realId.equals("anonymous@anonymous.com")) {
				throw new BusinessException(ResponseCode.INVALID_LOGIN);
			}

			Users user = usersRepository.findByEmail(realId)
				.orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

			LoginDto userDto = LoginDto.builder()
				.accountId(user.getEmail())
				.password(user.getPassword())
				.role(user.getRole())
				.uuid(UUIDUtil.bytesToHex(user.getUserAiFeature().getUuid()))
				.build();

			return new CustomUser(userDto);
		} else {
			throw new UsernameNotFoundException("유효하지 않은 로그인 요청입니다.");
		}

	}
}
