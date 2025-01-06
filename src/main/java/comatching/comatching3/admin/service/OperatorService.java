package comatching.comatching3.admin.service;

import comatching.comatching3.admin.dto.request.BlackUserReq;
import comatching.comatching3.admin.dto.request.EmailVerifyReq;
import comatching.comatching3.admin.dto.request.ResetPasswordReq;
import comatching.comatching3.admin.dto.request.SendResetPasswordEmailReq;
import comatching.comatching3.admin.dto.request.SchoolEmailReq;
import comatching.comatching3.admin.dto.response.AfterVerifyEmailRes;
import comatching.comatching3.admin.dto.response.UserBasicInfoRes;
import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.enums.AdminRole;
import comatching.comatching3.admin.repository.AdminRepository;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.auth.jwt.JwtUtil;
import comatching.comatching3.users.auth.refresh_token.service.RefreshTokenService;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.users.service.BlackListService;
import comatching.comatching3.util.EmailUtil;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OperatorService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SecurityUtil securityUtil;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final EmailUtil emailUtil;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final BlackListService blackListService;
    private final UsersRepository usersRepository;

    private String RESET_LINK = "http://localhost:8080/admin/reset-password";

    // 이메일 전송하면서 관리자의 인증용 이메일 등록까지 같이 수행
    // todo: 이메일 양식 꾸며야 함
    @Transactional
    public String sendVerifyEmail(SchoolEmailReq schoolEmailReq) {

        Admin admin = securityUtil.getAdminFromContext();
        admin.setSchoolEmail(schoolEmailReq.getSchoolEmail());
        adminRepository.save(admin);

        String verificationCode = String.valueOf(new Random().nextInt(900000) + 100000);
        String token = UUID.randomUUID().toString();
        String redisKey = "email-verification:" + token;

        redisTemplate.opsForValue().set(redisKey, verificationCode, 10, TimeUnit.MINUTES);
        emailUtil.sendEmail(schoolEmailReq.getSchoolEmail(), "COMAtching 관리자 인증 메일", "Your verification code is " + verificationCode);

        return token;
    }

    @Transactional
    public AfterVerifyEmailRes verifyCode(EmailVerifyReq request) {
        String redisKey = "email-verification:" + request.getToken();
        String storedCode = (String) redisTemplate.opsForValue().get(redisKey);

        AfterVerifyEmailRes response = new AfterVerifyEmailRes();
        if (storedCode != null && storedCode.equals(request.getCode())) {
            Admin admin = securityUtil.getAdminFromContext();
            admin.changeAdminRole(admin.getAdminRole().equals(AdminRole.ROLE_SEMI_ADMIN) ? AdminRole.ROLE_ADMIN : AdminRole.ROLE_OPERATOR);
            admin.universityAuthOk();
            adminRepository.save(admin);

            String uuid = UUIDUtil.bytesToHex(admin.getUuid());
            String accessToken = jwtUtil.generateAccessToken(uuid, String.valueOf(admin.getAdminRole()));
            String refreshToken = refreshTokenService.getRefreshToken(uuid);
            redisTemplate.delete(redisKey);
            refreshTokenService.saveRefreshTokenInRedis(uuid, refreshToken);

            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);
            response.setSuccess(true);
            return response;
        }
        response.setAccessToken(null);
        response.setRefreshToken(null);
        response.setSuccess(false);
        return response;
    }

    @Transactional
    public void sendFindIdEmail(String schoolEmail) {
        Admin admin = adminRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        emailUtil.sendEmail(schoolEmail, "COMAtching 관리자 아이디 찾기 메일", admin.getNickname() + "님의 아이디는: " + admin.getAccountId() + "입니다.");
    }

    /**
     * 비밀번호 재설정 요청 메소드
     * @param req 계정 아이디, 학교 이메일
     */
    @Transactional
    public void sendResetPasswordEmail(SendResetPasswordEmailReq req) {
        Admin admin = adminRepository.findBySchoolEmail(req.getSchoolEmail())
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        if (!admin.getAccountId().equals(req.getAccountId())) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }

        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(token, req.getSchoolEmail(), 24, TimeUnit.HOURS);

        String resetLink = RESET_LINK + "?token=" + token;

        emailUtil.sendEmail(req.getSchoolEmail(), "비밀번호 재설정 이메일입니다.", "비밀번호를 재설정 하려면 다음 링크를 클릭하세요: " + resetLink);
    }

    /**
     * 비밀번호 재설정 메소드
     * @param req 토큰, 비밀번호, 확인 비밀번호
     * 비밀번호 재설정이 완료되면 토큰 만료
     * todo: 하루에 5번만 재설정할 수 있도록 제한을 둘지 고민
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

    public Boolean checkEmailDuplicate(String schoolEmail) {
        return adminRepository.existsBySchoolEmail(schoolEmail);
    }

    public Boolean checkEmailDomain(String schoolEmail) {
        String mailDomain = securityUtil.getAdminFromContext().getUniversity().getMailDomain();

        String[] emailParts = schoolEmail.split("@");

        if (emailParts.length != 2) {
            return false;
        }

        String domain = emailParts[1];
        return domain.equalsIgnoreCase(mailDomain);
    }

    /**
     * 유저, 오퍼레이터 블랙리스트 추가하는 메서드
     * @param blackUserReq uuid, reason
     */
    public void blackUser(BlackUserReq blackUserReq) {
        Admin admin = securityUtil.getAdminFromContext();
        byte[] userUuid = UUIDUtil.uuidStringToBytes(blackUserReq.getUuid());

        if (blackListService.checkBlackList(userUuid)) {
            throw new BusinessException(ResponseCode.BLACK_USER);
        }

        if (usersRepository.existsByUserUuid(userUuid)) {
            Users user = usersRepository.findUsersByUuid(userUuid).orElseThrow(
                () -> new BusinessException(ResponseCode.USER_NOT_FOUND));

            blackListService.addBlackList(user, blackUserReq.getReason());
            return;
        }

        if (adminRepository.existsByUuid(userUuid)) {
            if (admin.getAdminRole().equals(AdminRole.ROLE_OPERATOR)) {
                throw new BusinessException(ResponseCode.NO_PERMISSION);
            }

            Admin targetAdmin = adminRepository.findByUuid(userUuid).orElseThrow(
                () -> new BusinessException(ResponseCode.USER_NOT_FOUND));

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

    public Page<UserBasicInfoRes> getUserBasicInfoList(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Users> usersPage = usersRepository.searchUsersByKeyword(keyword, pageable);

        return usersPage.map(user -> UserBasicInfoRes.builder()
            .uuid(UUIDUtil.bytesToHex(user.getUserAiFeature().getUuid()))
            .username(user.getUsername())
            .email(user.getEmail())
            .provider(user.getProvider())
            .point(user.getPoint())
            .pickMe(user.getPickMe())
            .warnCount(user.getWarningCount())
            .registerAt(user.getCreatedAt())
            .build());
    }
}
