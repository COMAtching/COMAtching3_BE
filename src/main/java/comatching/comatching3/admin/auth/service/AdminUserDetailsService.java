package comatching.comatching3.admin.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import comatching.comatching3.admin.auth.AdminDto;
import comatching.comatching3.admin.auth.CustomAdmin;
import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.repository.AdminRepository;
import comatching.comatching3.util.UUIDUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {
	private final AdminRepository adminRepository;

	@Override
	public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
		Admin admin = adminRepository.findByAccountId(accountId)
			.orElseThrow(() -> new UsernameNotFoundException("관리자를 찾을 수 없습니다."));

		AdminDto adminDto = AdminDto.builder()
			.accountId(admin.getAccountId())
			.password(admin.getPassword())
			.role(admin.getAdminRole().getRoleName())
			.uuid(UUIDUtil.bytesToHex(admin.getUuid()))
			.build();

		return new CustomAdmin(adminDto);
	}
}
