package comatching.comatching3.auth.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import comatching.comatching3.admin.auth.AdminDto;
import comatching.comatching3.admin.auth.CustomAdmin;
import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.repository.AdminRepository;
import comatching.comatching3.users.auth.oauth2.dto.UserDto;
import comatching.comatching3.users.auth.oauth2.provider.CustomUser;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.UUIDUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomDetailsService implements UserDetailsService {
	private final AdminRepository adminRepository;
	private final UsersRepository usersRepository;

	@Override
	public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {

		Optional<Users> userOpt = usersRepository.findByAccountId(accountId);

		if (userOpt.isEmpty()) {
			Admin admin = adminRepository.findByAccountId(accountId)
				.orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));

			AdminDto adminDto = AdminDto.builder()
				.accountId(admin.getAccountId())
				.password(admin.getPassword())
				.role(admin.getAdminRole().getRoleName())
				.uuid(UUIDUtil.bytesToHex(admin.getUuid()))
				.build();

			return new CustomAdmin(adminDto);

		} else {
			Users user = userOpt.get();

			UserDto userDto = UserDto.builder()
				.uuid(UUIDUtil.bytesToHex(user.getUserAiFeature().getUuid()))
				.role(user.getRole())
				.build();

			return new CustomUser(userDto);
		}


	}
}
