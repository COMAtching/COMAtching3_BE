package comatching.comatching3.admin.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import comatching.comatching3.admin.dto.request.BlackUserReq;
import comatching.comatching3.admin.dto.request.ResetPasswordReq;
import comatching.comatching3.admin.dto.request.SendResetPasswordEmailReq;
import comatching.comatching3.admin.dto.response.UserBasicInfoRes;
import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.entity.University;
import comatching.comatching3.admin.enums.AdminRole;
import comatching.comatching3.admin.repository.AdminRepository;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.history.dto.res.GenderRes;
import comatching.comatching3.history.enums.PointHistoryType;
import comatching.comatching3.history.service.PointHistoryService;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UserAiFeatureRepository;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.users.service.BlackListService;
import comatching.comatching3.util.EmailUtil;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OperatorService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final SecurityUtil securityUtil;
	private final EmailUtil emailUtil;
	private final PasswordEncoder passwordEncoder;
	private final BlackListService blackListService;
	private final PointHistoryService pointHistoryService;
	private final UniversityService universityService;
	private final AdminRepository adminRepository;
	private final UsersRepository usersRepository;
	private final UserAiFeatureRepository userAiFeatureRepository;

	private final String RESET_LINK = "https://backend.comatching.site/admin/reset-password";

	/**
	 * 아이디 찾기 메일 전송
	 */
	@Transactional
	public void sendFindIdEmail(String schoolEmail) {
		Admin admin = adminRepository.findBySchoolEmail(schoolEmail)
			.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

		emailUtil.sendEmail(schoolEmail, "코매칭 관리자 아이디 찾기 메일",
			admin.getNickname() + "님의 아이디는: " + admin.getAccountId() + "입니다.");
	}

	/**
	 * 비밀번호 재설정 메일 전송
	 * @param req 계정 아이디, 학교 이메일
	 */
	@Transactional
	public void sendResetPasswordEmail(SendResetPasswordEmailReq req) {
		Admin admin = adminRepository.findBySchoolEmail(req.getEmail())
			.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

		if (!admin.getAccountId().equals(req.getAccountId())) {
			throw new BusinessException(ResponseCode.USER_NOT_FOUND);
		}

		String token = UUID.randomUUID().toString();
		redisTemplate.opsForValue().set(token, req.getEmail(), 10, TimeUnit.MINUTES);

		String resetLink = RESET_LINK + "?token=" + token;

		emailUtil.sendEmail(req.getEmail(), "비밀번호 재설정 이메일입니다.", "비밀번호를 재설정 하려면 다음 링크를 클릭하세요: " + resetLink);
	}

	/**
	 * 비밀번호 재설정 메소드
	 * @param req 토큰, 비밀번호, 확인 비밀번호
	 * 비밀번호 재설정이 완료되면 토큰 만료
	 */
	@Transactional
	public void resetPassword(ResetPasswordReq req) {
		if (!req.getPassword().equals(req.getConfirmPassword())) {
			throw new BusinessException(ResponseCode.ARGUMENT_NOT_VALID);
		}

		Object emailObj = redisTemplate.opsForValue().get(req.getToken());
		if (emailObj == null) {
			throw new BusinessException(ResponseCode.TOKEN_NOT_AVAILABLE);
		}
		String schoolEmail = String.valueOf(emailObj);

		Admin admin = adminRepository.findBySchoolEmail(schoolEmail)
			.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

		String encryptedPassword = passwordEncoder.encode(req.getPassword());
		admin.updatePassword(encryptedPassword);

		adminRepository.save(admin);

		redisTemplate.delete(req.getToken());
	}

	public boolean checkEmailDuplicate(String schoolEmail) {
		return adminRepository.existsBySchoolEmail(schoolEmail);
	}

	/**
	 * 유저, 오퍼레이터 블랙리스트 추가하는 메서드
	 * @param blackUserReq uuid, reason
	 */
	public void blackUser(BlackUserReq blackUserReq) {
		Admin admin = securityUtil.getAdminFromContext();
		University university = admin.getUniversity();
		byte[] userUuid = UUIDUtil.uuidStringToBytes(blackUserReq.getUuid());

		if (blackListService.checkBlackListByUuid(userUuid)) {
			throw new BusinessException(ResponseCode.BLACK_USER);
		}

		if (usersRepository.existsByUserUuid(userUuid)) {
			Users user = usersRepository.findUsersByUuid(userUuid).orElseThrow(
				() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

			if (!university.equals(user.getUniversity())) {
				throw new BusinessException(ResponseCode.NO_PERMISSION);
			}
			blackListService.addBlackList(user, blackUserReq.getReason());
			return;
		}

		if (adminRepository.existsByUuid(userUuid)) {
			if (admin.getAdminRole().equals(AdminRole.ROLE_OPERATOR)) {
				throw new BusinessException(ResponseCode.NO_PERMISSION);
			}

			Admin targetAdmin = adminRepository.findByUuid(userUuid).orElseThrow(
				() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

			if (!university.equals(targetAdmin.getUniversity())) {
				throw new BusinessException(ResponseCode.NO_PERMISSION);
			}

			blackListService.addBlackList(targetAdmin, blackUserReq.getReason());
		}

		throw new BusinessException(ResponseCode.ARGUMENT_NOT_VALID);
	}

	/**
	 * 블랙 해제 기능
	 * 최고관리자는 오퍼레이터, 유저 해제 가능
	 * 오퍼레이터는 유저만 해제 가능
	 * @param uuid
	 */
	public void unBlackUser(String uuid) {
		Admin admin = securityUtil.getAdminFromContext();
		byte[] userUuid = UUIDUtil.uuidStringToBytes(uuid);

		if (!usersRepository.existsByUserUuid(userUuid) && !adminRepository.existsByUuid(userUuid)) {
			throw new BusinessException(ResponseCode.USER_NOT_FOUND);
		}

		if (admin.getAdminRole().equals(AdminRole.ROLE_OPERATOR) && adminRepository.existsByUuid(userUuid)) {
			throw new BusinessException(ResponseCode.NO_PERMISSION);
		}

		blackListService.removeBlackList(userUuid);
	}

	/**
	 * 유저 전체 조회 (검색 기능 포함)
	 * 같은 학교만 가능
	 * @param searchType 검색 유형 (null, 'email', 'username')
	 * @param keyword 검색어
	 * @param page 페이지 번호
	 * @param size 페이지 크기
	 */
	public Page<UserBasicInfoRes> getUserBasicInfoList(String searchType, String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		University university = securityUtil.getAdminFromContext().getUniversity();
		Page<Users> usersPage;

		if (searchType == null || keyword == null || keyword.isEmpty()) {
			// 검색 조건이 없을 경우 전체 조회
			usersPage = usersRepository.findALlByUniversityOrderByCreatedAtAsc(pageable, university);
		} else if ("email".equalsIgnoreCase(searchType)) {
			// 이메일로 검색
			usersPage = usersRepository.findAllByUniversityAndEmailContainingIgnoreCaseOrderByCreatedAtAsc(
				university, keyword, pageable);
		} else if ("username".equalsIgnoreCase(searchType)) {
			// 사용자명으로 검색
			usersPage = usersRepository.searchByUsernameOrRealName(
				university, keyword, pageable);
		} else {
			// 잘못된 검색 유형일 경우 전체 조회
			usersPage = usersRepository.findALlByUniversityOrderByCreatedAtAsc(pageable, university);
		}

		return usersPage.map(user -> UserBasicInfoRes.builder()
			.uuid(UUIDUtil.bytesToHex(user.getUserAiFeature().getUuid()))
			.username(user.getUsername())
			.email(user.getEmail())
			.provider(user.getProvider())
			.point(user.getPoint())
			.warnCount(user.getWarningCount())
			.registerAt(user.getCreatedAt())
			.realName(user.getRealName())
			.build());
	}

	/**
	 * 닉네임으로 검색
	 * 같은 학교만 가능
	 * @param username 닉네임
	 */
	public UserBasicInfoRes getUserBasicInfoByUsername(String username) {

		University university = securityUtil.getAdminFromContext().getUniversity();

		Users user = usersRepository.findByUsernameAndUniversity(username, university)
			.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

		return UserBasicInfoRes.builder()
			.uuid(UUIDUtil.bytesToHex(user.getUserAiFeature().getUuid()))
			.username(user.getUsername())
			.email(user.getEmail())
			.provider(user.getProvider())
			.point(user.getPoint())
			.warnCount(user.getWarningCount())
			.registerAt(user.getCreatedAt())
			.build();
	}

	/**
	 * 이메일로 검색
	 * 같은 학교만 가능
	 * @param email
	 * @return
	 */
	public UserBasicInfoRes getUserBasicInfoByEmail(String email) {

		University university = securityUtil.getAdminFromContext().getUniversity();

		Users user = usersRepository.findByEmailAndUniversity(email, university)
			.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

		return UserBasicInfoRes.builder()
			.uuid(UUIDUtil.bytesToHex(user.getUserAiFeature().getUuid()))
			.username(user.getUsername())
			.email(user.getEmail())
			.provider(user.getProvider())
			.point(user.getPoint())
			.warnCount(user.getWarningCount())
			.registerAt(user.getCreatedAt())
			.build();
	}

	public UserBasicInfoRes getUserBasicInfoByUuid(String uuid) {

		University university = securityUtil.getAdminFromContext().getUniversity();

		Users user = usersRepository.findUsersByUuidAndUniversity(UUIDUtil.uuidStringToBytes(uuid), university)
			.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

		return UserBasicInfoRes.builder()
			.uuid(UUIDUtil.bytesToHex(user.getUserAiFeature().getUuid()))
			.username(user.getUsername())
			.email(user.getEmail())
			.provider(user.getProvider())
			.point(user.getPoint())
			.warnCount(user.getWarningCount())
			.registerAt(user.getCreatedAt())
			.build();
	}

	/**
	 * 유저 포인트 수동 조작
	 * 같은 학교만 가능
	 * @param uuid
	 * @param point
	 */
	@Transactional
	public void changeUserPoint(String uuid, Long point, String reason) {
		byte[] userUuid = UUIDUtil.uuidStringToBytes(uuid);
		Users user = usersRepository.findUsersByUuid(userUuid).orElseThrow(
			() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

		universityService.checkUniversity(user, null);

		if (point < 0 && user.getPoint() < Math.abs(point)) {
			throw new BusinessException(ResponseCode.BAD_REQUEST);
		}

		user.addPayedPoint(point);
		user.addPoint(point);
		pointHistoryService.makePointHistoryWithReason(user, PointHistoryType.ADMIN_CONTROL, point, reason);
	}

	/**
	 * 남녀 성비 확인
	 * 같은 학교만 가능
	 */
	public GenderRes getGenderRatio() {
		String universityName = securityUtil.getAdminFromContext().getUniversity().getUniversityName();

		int male = userAiFeatureRepository.getManRatio(universityName);
		int female = userAiFeatureRepository.getWomanRatio(universityName);

		return GenderRes.builder()
			.man(male)
			.woman(female)
			.build();
	}
}
