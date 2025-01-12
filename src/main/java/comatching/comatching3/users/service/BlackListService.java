package comatching.comatching3.users.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.service.UniversityService;
import comatching.comatching3.users.dto.response.BlackListRes;
import comatching.comatching3.users.entity.BlackList;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.BlackListRepository;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlackListService {

	private final BlackListRepository blackListRepository;
	private final SecurityUtil securityUtil;
	private final UniversityService universityService;

	/**
	 * User 블랙 리스트 추가
	 * 같은 학교만 가능
	 * @param user
	 */
	@Transactional
	public void addBlackList(Users user, String reason) {

		universityService.checkUniversity(user, null);

		BlackList blackUser = BlackList.builder()
			.username(user.getUsername())
			.university(user.getUniversity().getUniversityName())
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
	 * 같은 학교만 가능
	 * @param admin
	 */
	@Transactional
	public void addBlackList(Admin admin, String reason) {

		universityService.checkUniversity(null, admin);

		BlackList blackAdmin = BlackList.builder()
			.email(admin.getSchoolEmail())
			.username(admin.getNickname())
			.university(admin.getUniversity().getUniversityName())
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

	/**
	 * 블랙 여부 체크
	 * @param uuid
	 */
	public boolean checkBlackListByUuid(byte[] uuid) {
		return blackListRepository.existsByUuid(uuid);
	}

	public boolean checkBlackListByEmail(String email) {
		return blackListRepository.existsByEmail(email);
	}


	/**
	 * 블랙리스트 조회
	 * 같은 학교만 가능
	 * @return
	 */
	public List<BlackListRes> getAllBlackList() {

		String universityName = securityUtil.getAdminFromContext().getUniversity().getUniversityName();

		return blackListRepository.findAllByUniversityOrderByCreatedAtDesc(universityName).stream()
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
