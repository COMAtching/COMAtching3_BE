package comatching.comatching3.admin.service;

import comatching.comatching3.admin.dto.request.EmailVerifyReq;
import comatching.comatching3.admin.dto.request.SchoolEmailReq;
import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.enums.AdminRole;
import comatching.comatching3.admin.repository.AdminRepository;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.util.EmailUtil;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final EmailUtil emailUtil;
    private final AdminRepository adminRepository;

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
    public Boolean verifyCode(EmailVerifyReq request) {
        String redisKey = "email-verification:" + request.getToken();
        String storedCode = (String) redisTemplate.opsForValue().get(redisKey);

        if (storedCode != null && storedCode.equals(request.getCode())) {
            Admin admin = securityUtil.getAdminFromContext();
            admin.changeAdminRole(admin.getAdminRole().equals(AdminRole.ROLE_SEMI_ADMIN) ? AdminRole.ROLE_ADMIN : AdminRole.ROLE_OPERATOR);
            adminRepository.save(admin);

            redisTemplate.delete(redisKey);
            return true;
        }
        return false;
    }

    // todo: 아이디를 다 알려주지말고 조금만 보여줄지 고민
    @Transactional
    public void sendFindIdEmail(String schoolEmail) {
        Admin admin = adminRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        emailUtil.sendEmail(schoolEmail, "COMAtching 관리자 아이디 찾기 메일", admin.getNickname() + "님의 아이디는: " + admin.getAccountId() + "입니다.");
    }

    /**
     * 비밀번호 재설정 메소드
     * @param schoolEmailReq
     * todo: 토큰 발행 후 레디스에 이메일 - 토큰으로 저장, 만료 시간 부여
     * todo: 비밀번호 재설정 URL에 파라미터로 토큰을 넣어 무작위성 부여
     * todo: 링크 접속 시 레디스의 토큰이 있는지 확인 (있으면 유효, 없으면 유효하지 않은 토큰)
     * todo: 비밀번호 재설정 후 레디스에서 삭제해 토큰 만료시킴
     */
    @Transactional
    public void updatePassword(SchoolEmailReq schoolEmailReq) {
        Admin admin = adminRepository.findBySchoolEmail(schoolEmailReq.getSchoolEmail())
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));


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
