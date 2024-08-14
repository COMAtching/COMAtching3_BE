package comatching.comatching3.admin.service;

import comatching.comatching3.admin.dto.request.*;
import comatching.comatching3.admin.dto.response.AdminInfoRes;
import comatching.comatching3.admin.dto.response.TokenRes;
import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.entity.University;
import comatching.comatching3.admin.enums.AdminRole;
import comatching.comatching3.admin.exception.AccountIdDuplicatedException;
import comatching.comatching3.admin.exception.UniversityNotExistException;
import comatching.comatching3.admin.repository.AdminRepository;
import comatching.comatching3.admin.repository.UniversityRepository;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.auth.jwt.JwtUtil;
import comatching.comatching3.users.auth.refresh_token.service.RefreshTokenService;
import comatching.comatching3.util.EmailUtil;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final UniversityRepository universityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailUtil emailUtil;

    /**
     * 관리자 회원가입
     * 다른 학교의 관리자를 가입시킬 때, 우리쪽에서 다른 학교 관리자 계정 임시로 생성해주는 것도 가능
     * @param form 관리자 회원 가입에 필요한 DTO
     */
    @Transactional
    public void adminRegister(AdminRegisterReq form) {

        Optional<Admin> existAdmin = adminRepository.findByAccountId(form.getAccountId());

        if (existAdmin.isPresent()) {
            throw new AccountIdDuplicatedException("ACCOUNT_ID_DUPLICATED");
        }

        String encryptedPassword = passwordEncoder.encode(form.getPassword());
        AdminRole role = AdminRole.valueOf(form.getRole());
        Optional<University> universityOptional = universityRepository.findByUniversityName(form.getUniversity());

        if (universityOptional.isEmpty()) {
            throw new UniversityNotExistException("학교 정보가 존재하지 않습니다.");
        }

        Admin admin = Admin.builder()
                .accountId(form.getAccountId())
                .password(encryptedPassword)
                .uuid(UUIDUtil.createUUID())
                .nickname(universityOptional.get().getUniversityName() + " 관리자")
                .adminRole(role)
                .university(universityOptional.get())
                .build();

        adminRepository.save(admin);
    }

    /**
     * 관리자 로그인
     * @param form 관리자 ID, PW
     * @return ACCESS, REFRESH TOKEN
     */

    public TokenRes adminLogin(AdminLoginReq form) {

        Optional<Admin> existAdmin = adminRepository.findByAccountId(form.getAccountId());

        if (existAdmin.isEmpty()) {
            throw new BusinessException(ResponseCode.INVALID_LOGIN);
        }

        Admin admin = existAdmin.get();

        if (!passwordEncoder.matches(form.getPassword(), admin.getPassword())) {
            throw new BusinessException(ResponseCode.INVALID_LOGIN);
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
    }

    // 이메일 전송하면서 관리자의 인증용 이메일 등록까지 같이 수행
    @Transactional
    public String sendVerifyEmail(SchoolEmailReq schoolEmailReq) {

        Admin admin = getAdminFromContext();
        admin.setSchoolEmail(schoolEmailReq.getSchoolEmail());
        adminRepository.save(admin);

        String verificationCode = String.valueOf(new Random().nextInt(900000) + 100000);
        String token = UUID.randomUUID().toString();
        String redisKey = "email-verification:" + token;

        redisTemplate.opsForValue().set(redisKey, verificationCode, 10, TimeUnit.MINUTES);
        emailUtil.sendEmail(schoolEmailReq.getSchoolEmail(), "COMAtching 관리자 인증 메일", "Your verification code is " + verificationCode);

        return token;
    }

    // 역할로 이메일 인증을 구분할거면 굳이 isEmailVerified 필드가 필요한가 싶음.
    @Transactional
    public Boolean verifyCode(EmailVerifyReq request) {
        String redisKey = "email-verification:" + request.getToken();
        String storedCode = (String) redisTemplate.opsForValue().get(redisKey);

        if (storedCode != null && storedCode.equals(request.getCode())) {
            Admin admin = getAdminFromContext();
            admin.changeAdminRole(admin.getAdminRole().equals(AdminRole.ROLE_SEMI_ADMIN) ? AdminRole.ROLE_ADMIN : AdminRole.ROLE_OPERATOR);
            admin.emailVerifiedSuccess();
            adminRepository.save(admin);

            redisTemplate.delete(redisKey);
            return true;
        }
        return false;
    }

    public AdminInfoRes getAdminInfo() {
        Admin admin = getAdminFromContext();

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

    public Boolean checkEmailDuplicate(String schoolEmail) {
        return adminRepository.existsBySchoolEmail(schoolEmail);
    }

    public Boolean checkEmailDomain(String schoolEmail) {
        String mailDomain = getAdminFromContext().getUniversity().getMailDomain();

        String[] emailParts = schoolEmail.split("@");

        if (emailParts.length != 2) {
            return false;
        }

        String domain = emailParts[1];
        return domain.equalsIgnoreCase(mailDomain);
    }

    /**
     * 관리자 정보 변경 메소드 (계정 ID는 1번만 가능, 닉네임, 연락용 이메일, 앱 이름)
     * @param request 바꿀 항목의 정보 (바꾸지 않을 항목은 null)
     */
    @Transactional
    public void updateAdminInfo(AdminInfoUpdateReq request) {
        Admin admin = getAdminFromContext();

        if (request.getAccountId().isPresent()) {
            if (admin.getAccountIdChanged()) {
                throw new BusinessException(ResponseCode.BAD_REQUEST);
            }
            admin.updateAccountId(request.getAccountId().get());
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
                    .orElseThrow(() -> new UniversityNotExistException("존재하지 않는 대학교 정보"));

            university.updateAppName(request.getAppName().get());
            universityRepository.save(university);
        }

        adminRepository.save(admin);
    }


    private Admin getAdminFromContext() {
        String adminUuid = SecurityUtil.getCurrentUserUUID()
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        return adminRepository.findByUuid(UUIDUtil.uuidStringToBytes(adminUuid))
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }
}
