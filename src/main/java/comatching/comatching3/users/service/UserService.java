package comatching.comatching3.users.service;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.dto.UserFeatureReq;
import comatching.comatching3.users.dto.UserInfoRes;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.ContactFrequency;
import comatching.comatching3.users.enums.Gender;
import comatching.comatching3.users.enums.Hobby;
import comatching.comatching3.users.enums.Role;
import comatching.comatching3.users.repository.UserAiFeatureRepository;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final UserAiFeatureRepository userAiFeatureRepository;

    /**
     * 소셜 유저 피처 정보 입력 후 유저로 역할 변경까지
     * @param form social 유저의 Feature
     */
    public void inputUserInfo(UserFeatureReq form) {

        Users user = getUsersFromContext();
        List<Hobby> hobbyList = form.getHobby().stream()
                .map(Hobby::valueOf)
                .toList();

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

        usersRepository.save(user);
    }

    /**
     * 메인 페이지 유저 정보 조회
     * @return 유저 정보 조회
     */
    public UserInfoRes getUserInfo() {

        Users user = getUsersFromContext();

        return UserInfoRes.builder()
                .username(user.getUsername())
                .major(user.getUserAiFeature().getMajor())
                .admissionYear(user.getUserAiFeature().getAdmissionYear())
                .song(user.getSong())
                .mbti(user.getUserAiFeature().getMbti())
                .point(user.getPoint())
                .pickMe(user.getPickMe())
                .build();
    }

    /**
     * 유저 포인트 조회
     * @return 유저 포인트
     */
    public Integer getPoints() {
        Users user = getUsersFromContext();
        return user.getPoint();
    }

    private Users getUsersFromContext() {
        Optional<String> userUUIDOptional = SecurityUtil.getCurrentUserUUID();

        if (userUUIDOptional.isEmpty()) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }

        String uuid = userUUIDOptional.get();
        byte[] byteUUID = UUIDUtil.uuidStringToBytes(uuid);
        Optional<UserAiFeature> userOptional = userAiFeatureRepository.findByUuid(byteUUID);

        if (userOptional.isEmpty()) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }

        return userOptional.get().getUsers();
    }
}
