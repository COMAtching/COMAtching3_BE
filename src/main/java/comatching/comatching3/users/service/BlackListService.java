package comatching.comatching3.users.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.enums.AdminRole;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.dto.response.BlackListRes;
import comatching.comatching3.users.entity.BlackList;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.Role;
import comatching.comatching3.users.repository.BlackListRepository;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlackListService {

	private final BlackListRepository blackListRepository;

	/**
	 * User 블랙 리스트 추가
	 * @param user
	 */
	@Transactional
	public void addBlackList(Users user, String reason) {
		BlackList blackUser = BlackList.builder()
			.username(user.getUsername())
			.email(user.getEmail())
			.uuid(user.getUserAiFeature().getUuid())
			.role(user.getRole())
			.provider(user.getProvider())
			.reason(reason)
			.build();

		blackListRepository.save(blackUser);
	}

	/**
	 * Admin 블랙 리스트 추가
	 * @param admin
	 */
	@Transactional
	public void addBlackList(Admin admin, String reason) {
		BlackList blackAdmin = BlackList.builder()
			.email(admin.getSchoolEmail())
			.username(admin.getNickname())
			.role(admin.getAdminRole().getRoleName())
			.uuid(admin.getUuid())
			.provider("Admin")
			.reason(reason)
			.build();

		blackListRepository.save(blackAdmin);
	}

	/**
	 * 블랙리스트 해제
	 * @param uuid
	 */
	@Transactional
	public void removeBlackList(byte[] uuid) {
		blackListRepository.deleteByUuid(uuid);
	}

	public boolean checkBlackList(byte[] uuid) {
		return blackListRepository.existsByUuid(uuid);
	}

	public List<BlackListRes> getAllBlackList() {
		return blackListRepository.findAll().stream()
			.map(blackList -> BlackListRes.builder()
				.uuid(UUIDUtil.bytesToHex(blackList.getUuid()))
				.username(blackList.getUsername())
				.role(blackList.getRole())
				.reason(blackList.getReason())
				.blackedAt(blackList.getCreatedAt().toString())
				.build())
			.toList();
	}

}
