package comatching.comatching3.users.service;


import java.util.List;

import comatching.comatching3.admin.entity.University;
import comatching.comatching3.admin.repository.UniversityRepository;
import comatching.comatching3.users.dto.BuyPickMeReq;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import comatching.comatching3.admin.dto.response.TokenRes;
import comatching.comatching3.charge.repository.ChargeRequestRepository;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.auth.jwt.JwtUtil;
import comatching.comatching3.users.auth.refresh_token.service.RefreshTokenService;
import comatching.comatching3.users.dto.UserFeatureReq;
import comatching.comatching3.users.dto.UserInfoRes;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.ContactFrequency;
import comatching.comatching3.users.enums.Gender;
import comatching.comatching3.users.enums.Hobby;
import comatching.comatching3.users.enums.Role;
import comatching.comatching3.users.enums.UserCrudType;
import comatching.comatching3.users.repository.UserAiFeatureRepository;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.RabbitMQ.UserCrudRabbitMQUtil;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final UserAiFeatureRepository userAiFeatureRepository;
    private final ChargeRequestRepository chargeRequestRepository;
    private final UniversityRepository universityRepository;
    private final SecurityUtil securityUtil;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserCrudRabbitMQUtil rabbitMQUtil;

    public Long getParticipations() {
        return usersRepository.count();
    }


    /**
     * 소셜 유저 피처 정보 입력 후 유저로 역할 변경까지
     * @param form social 유저의 Feature
     */
    @Transactional
    public TokenRes inputUserInfo(UserFeatureReq form) {

        Users user = securityUtil.getCurrentUsersEntity();
        List<Hobby> hobbyList = form.getHobby().stream()
                .map(Hobby::valueOf)
                .toList();

        University university = universityRepository.findByUniversityName(form.getUniversity())
                .orElseThrow(() -> new BusinessException(ResponseCode.ARGUMENT_NOT_VALID));

        UserAiFeature userAiFeature = user.getUserAiFeature();

        userAiFeature.updateMajor(form.getMajor());
        userAiFeature.updateGender(Gender.valueOf(form.getGender()));
        userAiFeature.updateAge(form.getAge());
        userAiFeature.updateMbti(form.getMbti());
        userAiFeature.updateHobby(hobbyList);
        userAiFeature.updateContactFrequency(ContactFrequency.valueOf(form.getContactFrequency()));
        userAiFeature.updateAdmissionYear(form.getAdmissionYear());

        userAiFeatureRepository.save(userAiFeature);

        user.updateSong(form.getSong());
        user.updateComment(form.getComment());
        user.updateRole(Role.USER.getRoleName());
        user.updateUniversity(university);

        usersRepository.save(user);

        //역할 업데이트된 jwt 토큰 발행 및 토큰 교체
        String uuid = UUIDUtil.bytesToHex(user.getUserAiFeature().getUuid());
        String accessToken = jwtUtil.generateAccessToken(uuid, Role.USER.getRoleName());
        String refreshToken = refreshTokenService.getRefreshToken(uuid);

        securityUtil.setAuthentication(accessToken);

        /**
         * csv 반영 요청 3번까지 요청 후 안되면 throw (최대 30초)
         */
        Boolean sendSuccess = rabbitMQUtil.sendUserChange(user.getUserAiFeature(), UserCrudType.CREATE);

        if(!sendSuccess){
            throw new BusinessException(ResponseCode.USER_REGISTER_FAIL);
        }

        return TokenRes.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }



    /**
     * 메인 페이지 유저 정보 조회
     * @return 유저 정보 조회
     */
    public UserInfoRes getUserInfo() {

        Users user = securityUtil.getCurrentUsersEntity();

        Boolean canRequest = !chargeRequestRepository.existsByUsers(user);

        return UserInfoRes.builder()
                .username(user.getUsername())
                .major(user.getUserAiFeature().getMajor())
                .age(user.getUserAiFeature().getAge())
                .song(user.getSong())
                .mbti(user.getUserAiFeature().getMbti())
                .point(user.getPoint())
                .pickMe(user.getPickMe())
                .canRequestCharge(canRequest)
                .build();
    }

    /**
     * 유저 포인트 조회
     * @return 유저 포인트
     */
    public Integer getPoints() {
        Users user = securityUtil.getCurrentUsersEntity();
        return user.getPoint();
    }

    @Transactional
    public void buyPickMe(BuyPickMeReq req) {
        Users user = securityUtil.getCurrentUsersEntity();
        int price = 500;
        int userPoint = user.getPoint();
        int reqPoint = req.getAmount() * price;

        if (reqPoint > userPoint) {
            throw new BusinessException(ResponseCode.NOT_ENOUGH_POINT);
        }

        user.subtractPoint(reqPoint);
        user.addPickMe(req.getAmount());
    }
}
