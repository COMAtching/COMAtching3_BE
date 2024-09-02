package comatching.comatching3.admin.service;

import comatching.comatching3.admin.dto.request.EmailVerifyReq;
import comatching.comatching3.admin.dto.request.ResetPasswordReq;
import comatching.comatching3.admin.dto.request.SendResetPasswordEmailReq;
import comatching.comatching3.admin.dto.request.SchoolEmailReq;
import comatching.comatching3.admin.dto.response.AfterVerifyEmailRes;
import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.enums.AdminRole;
import comatching.comatching3.admin.repository.AdminRepository;
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
            adminRepository.save(admin);

            String uuid = UUIDUtil.bytesToHex(admin.getUuid());
            String accessToken = jwtUtil.generateAccessToken(uuid, String.valueOf(admin.getAdminRole()));
            String refreshToken = refreshTokenService.getRefreshToken(uuid);

            securityUtil.setAuthentication(accessToken);

            redisTemplate.delete(redisKey);
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

    // todo: 아이디를 다 알려주지말고 조금만 보여줄지 고민
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
}
