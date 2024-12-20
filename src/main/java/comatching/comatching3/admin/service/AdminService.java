package comatching.comatching3.admin.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import comatching.comatching3.admin.dto.request.AdminInfoUpdateReq;
import comatching.comatching3.admin.dto.request.AdminRegisterReq;
import comatching.comatching3.admin.dto.response.AdminInfoRes;
import comatching.comatching3.admin.dto.response.OperatorRes;
import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.entity.University;
import comatching.comatching3.admin.enums.AdminRole;
import comatching.comatching3.admin.repository.AdminRepository;
import comatching.comatching3.admin.repository.UniversityRepository;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.auth.jwt.JwtUtil;
import comatching.comatching3.users.auth.refresh_token.service.RefreshTokenService;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

	private final AdminRepository adminRepository;
	private final UniversityRepository universityRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final RefreshTokenService refreshTokenService;
	private final SecurityUtil securityUtil;

	/**
	 * 관리자 회원가입
	 * 다른 학교의 관리자를 가입시킬 때, 우리쪽에서 다른 학교 관리자 계정 임시로 생성해주는 것도 가능
	 * @param form 관리자 회원 가입에 필요한 DTO
	 */
	@Transactional
	public void adminRegister(AdminRegisterReq form) {

		Boolean exist = adminRepository.existsAdminByAccountId(form.getAccountId());
		if (exist) {
			throw new BusinessException(ResponseCode.ACCOUNT_ID_DUPLICATED);
		}

		String encryptedPassword = passwordEncoder.encode(form.getPassword());
		AdminRole role = AdminRole.valueOf(form.getRole());
		Optional<University> universityOptional = universityRepository.findByUniversityName(form.getUniversity());

		if (universityOptional.isEmpty()) {
			throw new BusinessException(ResponseCode.SCHOOL_NOT_EXIST);
		}

		if (role.equals(AdminRole.ROLE_SEMI_ADMIN)) {
			Boolean adminExist = adminRepository.existsAdminByUniversity(universityOptional.get());
			if (adminExist) {
				throw new BusinessException(ResponseCode.BAD_REQUEST);
			}
		}

		Admin admin = Admin.builder()
			.accountId(form.getAccountId())
			.password(encryptedPassword)
			.uuid(UUIDUtil.createUUID())
			.nickname(form.getNickname())
			.adminRole(role)
			.university(universityOptional.get())
			.build();

		if (role.equals(AdminRole.ROLE_SEMI_ADMIN)) {
			admin.accessOk();
		}
		// else if (role.equals(AdminRole.ROLE_SEMI_OPERATOR)) {
		//     admin.accountIdChange();
		// }

		adminRepository.save(admin);
	}

	public Boolean isAccountDuplicated(String accountId) {
		return adminRepository.existsAdminByAccountId(accountId);
	}

	/**
	 * 승인 대기중인 오퍼레이터 목록 조회
	 * @return 승인 대기중인 오퍼레이터 목록 (uuid, 닉네임, 승인 여부)
	 */
	public List<OperatorRes> getPendingOperators() {
		return adminRepository.findAllAdminsByAccessFalse().stream()
			.map(admin -> OperatorRes.builder()
				.uuid(UUIDUtil.bytesToHex(admin.getUuid()))
				.nickname(admin.getNickname())
				.access(admin.getAccess())
				.build())
			.collect(Collectors.toList());
	}

	/**
	 * 오퍼레이터 승인 메소드
	 * @param uuid 승인할 오퍼레이터의 uuid
	 */
	@Transactional
	public void accessOperator(String uuid) {
		byte[] operatorUuid = UUIDUtil.uuidStringToBytes(uuid);
		Admin operator = adminRepository.findByUuid(operatorUuid)
			.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

		operator.accessOk();
		adminRepository.save(operator);
	}

	/**
	 * 관리자 로그인
	 * @param form 관리자 ID, PW
	 * @return ACCESS, REFRESH TOKEN
	 */

    /*@Transactional
    public TokenRes adminLogin(AdminLoginReq form) {

        log.info("admin login service logic 도착하니?");

        Admin admin = adminRepository.findByAccountId(form.getAccountId())
            .orElseThrow(() -> new BusinessException(ResponseCode.INVALID_LOGIN));

        if (!passwordEncoder.matches(form.getPassword(), admin.getPassword())) {
            throw new BusinessException(ResponseCode.INVALID_LOGIN);
        }

        if (!admin.getAccess()) {
            throw new BusinessException(ResponseCode.PENDING_OPERATOR);
        }

        String adminUuid = UUIDUtil.bytesToHex(admin.getUuid());
        String accessToken = jwtUtil.generateAccessToken(adminUuid, admin.getAdminRole().getRoleName());
        String refreshToken = refreshTokenService.getRefreshToken(adminUuid);

        if (refreshToken == null) {
            refreshToken = jwtUtil.generateRefreshToken(adminUuid, admin.getAdminRole().getRoleName());
            refreshTokenService.saveRefreshToken(adminUuid, refreshToken);
        }

        return TokenRes.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }*/

	/**
	 * 관리자 정보 조회
	 * @return 관리자 정보
	 */
	public AdminInfoRes getAdminInfo() {
		Admin admin = securityUtil.getAdminFromContext();
		System.out.println("real admin uuid = " + Arrays.toString(admin.getUuid()));

		return AdminInfoRes.builder()
			.accountId(admin.getAccountId())
			.nickname(admin.getNickname())
			.university(admin.getUniversity().getUniversityName())
			.role(admin.getAdminRole().toString())
			.schoolEmail(admin.getSchoolEmail())
			.contactEmail(admin.getContactEmail().orElse(null))
			.universityAuth(admin.getUniversityAuth())
			.accountIdChanged(admin.getAccountIdChanged())
			.appName(admin.getUniversity().getAppName())
			.build();

	}

	/**
	 * 관리자 정보 변경 메소드 (계정 ID는 1번만 가능, 닉네임, 연락용 이메일, 앱 이름)
	 * @param request 바꿀 항목의 정보 (바꾸지 않을 항목은 null)
	 */
	@Transactional
	public void updateAdminInfo(AdminInfoUpdateReq request) {
		Admin admin = securityUtil.getAdminFromContext();

		if (request.getAccountId().isPresent()) {
			if (admin.getAccountIdChanged()) {
				throw new BusinessException(ResponseCode.BAD_REQUEST);
			}
			admin.updateAccountId(request.getAccountId().get());
			admin.accountIdChange();
		}

		if (request.getNickname().isPresent()) {
			admin.updateNickname(request.getNickname().get());
		}

		if (request.getContactEmail().isPresent()) {
			admin.updateContactEmail(request.getContactEmail().get());
		}

		if (request.getAppName().isPresent()) {
			if (!admin.getAdminRole().equals(AdminRole.ROLE_ADMIN)) {
				throw new BusinessException(ResponseCode.BAD_REQUEST);
			}

			University university = universityRepository.findByUniversityName(admin.getUniversity().getUniversityName())
				.orElseThrow(() -> new BusinessException(ResponseCode.SCHOOL_NOT_EXIST));

			university.updateAppName(request.getAppName().get());
			universityRepository.save(university);
		}

		adminRepository.save(admin);
	}
}
