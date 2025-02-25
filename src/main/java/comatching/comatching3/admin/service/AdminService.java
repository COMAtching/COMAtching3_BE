package comatching.comatching3.admin.service;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vane.badwordfiltering.BadWordFiltering;

import comatching.comatching3.admin.dto.request.AdminInfoUpdateReq;
import comatching.comatching3.admin.dto.request.AdminRegisterReq;
import comatching.comatching3.admin.dto.request.EmailVerifyReq;
import comatching.comatching3.admin.dto.response.AdminInfoRes;
import comatching.comatching3.admin.dto.response.OperatorRes;
import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.entity.University;
import comatching.comatching3.admin.enums.AdminRole;
import comatching.comatching3.admin.repository.AdminRepository;
import comatching.comatching3.admin.repository.UniversityRepository;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.util.EmailUtil;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import comatching.comatching3.util.validation.AdditionalBadWords;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final AdminRepository adminRepository;
	private final UniversityRepository universityRepository;
	private final SecurityUtil securityUtil;
	private final EmailUtil emailUtil;
	private final PasswordEncoder passwordEncoder;
	private final SessionRepository<?> sessionRepository;

	/**
	 * 관리자 회원가입
	 *
	 * @param form 관리자 회원 가입에 필요한 DTO
	 */
	@Transactional
	public void adminRegister(AdminRegisterReq form) {

		checkNicknameFilter(form.getNickname());

		Boolean exist = adminRepository.existsAdminByAccountId(form.getAccountId());
		if (exist) {
			throw new BusinessException(ResponseCode.ACCOUNT_ID_DUPLICATED);
		}

		String encryptedPassword = passwordEncoder.encode(form.getPassword());
		AdminRole role = AdminRole.valueOf(form.getRole());

		University university = universityRepository.findByUniversityName(form.getUniversity())
			.orElseThrow(() -> new BusinessException(ResponseCode.SCHOOL_NOT_EXIST));

		if (role.equals(AdminRole.ROLE_SEMI_ADMIN)) {
			Boolean adminExist = adminRepository.existsAdminByUniversity(university);
			if (adminExist) {
				throw new BusinessException(ResponseCode.BAD_REQUEST);
			}
		}

		Admin admin = Admin.builder()
			.accountId(form.getAccountId())
			.password(encryptedPassword)
			.uuid(UUIDUtil.createUUID())
			.nickname(form.getNickname())
			.university(university)
			.adminRole(role)
			.schoolEmail(form.getSchoolEmail())
			.build();

		if (role.equals(AdminRole.ROLE_SEMI_ADMIN)) {
			admin.accessOk();
		}

		adminRepository.save(admin);
	}

	public Boolean isAccountDuplicated(String accountId) {
		return adminRepository.existsAdminByAccountId(accountId);
	}

	/**
	 * 인증용 이메일 전송
	 */
	@Transactional
	public String sendVerifyEmail() {
		Admin admin = securityUtil.getAdminFromContext();

		String email = admin.getSchoolEmail();

		String verificationCode = String.valueOf(new Random().nextInt(900000) + 100000);
		String token = UUID.randomUUID().toString();
		String redisKey = "email-verification:" + token;

		redisTemplate.opsForValue().set(redisKey, verificationCode, 3, TimeUnit.MINUTES);
		emailUtil.sendEmail(email, "COMAtching 관리자 인증 메일", "Your verification code is " + verificationCode);

		return token;
	}

	/**
	 * 이메일 인증번호 검사
	 */
	@Transactional
	public void verifyCode(EmailVerifyReq emailVerifyReq, HttpServletRequest request) {
		String redisKey = "email-verification:" + emailVerifyReq.getToken();
		String storedCode = (String)redisTemplate.opsForValue().get(redisKey);

		if (storedCode != null && storedCode.equals(emailVerifyReq.getCode())) {
			Admin admin = securityUtil.getAdminFromContext();
			admin.changeAdminRole(AdminRole.ROLE_ADMIN);
			admin.universityAuthOk();
			adminRepository.save(admin);

			securityUtil.setNewUserSecurityContext(admin, request);
		} else {
			throw new BusinessException(ResponseCode.ARGUMENT_NOT_VALID);
		}

	}

	/**
	 * 승인 대기중인 오퍼레이터 목록 조회
	 *
	 * @return 승인 대기중인 오퍼레이터 목록 (uuid, accountId, 닉네임, 요청 시각)
	 */
	public List<OperatorRes> getPendingOperators() {
		University university = securityUtil.getAdminFromContext().getUniversity();

		return adminRepository.findAllAdminsByUniversityAndAccessFalseOrderByCreatedAtDesc(university).stream()
			.map(admin -> OperatorRes.builder()
				.uuid(UUIDUtil.bytesToHex(admin.getUuid()))
				.accountId(admin.getAccountId())
				.nickname(admin.getNickname())
				.email(admin.getSchoolEmail())
				.requestAt(admin.getCreatedAt())
				.build())
			.collect(Collectors.toList());
	}

	/**
	 * 오퍼레이터 승인 메소드
	 *
	 * @param uuid 승인할 오퍼레이터의 uuid
	 */
	@Transactional
	public void accessOperator(String uuid) {

		University university = securityUtil.getAdminFromContext().getUniversity();

		byte[] operatorUuid = UUIDUtil.uuidStringToBytes(uuid);
		Admin operator = adminRepository.findByUuid(operatorUuid)
			.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

		if (!university.equals(operator.getUniversity())) {
			throw new BusinessException(ResponseCode.NO_PERMISSION);
		}

		operator.accessOk();
		operator.universityAuthOk();
		operator.changeAdminRole(AdminRole.ROLE_OPERATOR);
		adminRepository.save(operator);
	}

	/**
	 * 오퍼레이터 가입 거절 (오퍼레이터 계정은 삭제됨)
	 *
	 * @param uuid 오퍼레이터 id
	 */
	@Transactional
	public void denyOperator(String uuid) {

		University university = securityUtil.getAdminFromContext().getUniversity();

		byte[] operatorUuid = UUIDUtil.uuidStringToBytes(uuid);
		Admin operator = adminRepository.findByUuid(operatorUuid)
			.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

		if (!university.equals(operator.getUniversity())) {
			throw new BusinessException(ResponseCode.NO_PERMISSION);
		}

		if (operator.getAdminRole().equals(AdminRole.ROLE_ADMIN) || operator.getAccess()) {
			throw new BusinessException(ResponseCode.NO_PERMISSION);
		}
		adminRepository.delete(operator);
	}

	/**
	 * 관리자 정보 조회
	 *
	 * @return 관리자 정보
	 */
	public AdminInfoRes getAdminInfo() {
		Admin admin = securityUtil.getAdminFromContext();

		return AdminInfoRes.builder()
			.accountId(admin.getAccountId())
			.nickname(admin.getNickname())
			.university(admin.getUniversity().getUniversityName())
			.role(admin.getAdminRole().toString())
			.schoolEmail(admin.getSchoolEmail())
			.universityAuth(admin.getUniversityAuth())
			.build();

	}

	/**
	 * 관리자 정보 변경 메소드
	 * todo: 닉네임 검열
	 *
	 * @param request 닉네임만 바꿀 수 있음
	 */
	@Transactional
	public void updateAdminInfo(AdminInfoUpdateReq request) {
		Admin admin = securityUtil.getAdminFromContext();

		checkNicknameFilter(request.getNickname());
		admin.updateNickname(request.getNickname());
		adminRepository.save(admin);
	}

	private void checkNicknameFilter(String nickname) {
		BadWordFiltering badWordFiltering = new BadWordFiltering();
		badWordFiltering.addAll(List.of(AdditionalBadWords.koreaWord2));

		if (badWordFiltering.check(nickname)) {
			throw new BusinessException(ResponseCode.INVALID_USERNAME);
		}
	}

	/**
	 * 관리자 로그아웃
	 */
	public void adminLogout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);

		if (session != null) {
			String sessionId = session.getId();
			sessionRepository.deleteById(sessionId);
			session.invalidate();

			Cookie cookie = new Cookie("SESSION", null);
			cookie.setPath("/");
			cookie.setHttpOnly(true);
			cookie.setMaxAge(0);
			response.addCookie(cookie);

		} else {
			throw new BusinessException(ResponseCode.BAD_REQUEST);
		}
	}
}
