package comatching.comatching3.users.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vane.badwordfiltering.BadWordFiltering;

import comatching.comatching3.admin.dto.request.EmailVerifyReq;
import comatching.comatching3.admin.entity.University;
import comatching.comatching3.admin.repository.UniversityRepository;
import comatching.comatching3.admin.service.UniversityService;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.dto.AnonymousUser;
import comatching.comatching3.users.dto.messageQueue.CategoryReqMsg;
import comatching.comatching3.users.dto.request.UserFeatureReq;
import comatching.comatching3.users.dto.request.UserRegisterReq;
import comatching.comatching3.users.dto.request.UserUpdateInfoReq;
import comatching.comatching3.users.dto.response.CurrentPointRes;
import comatching.comatching3.users.dto.response.UserInfoRes;
import comatching.comatching3.users.dto.response.UsernamePointRes;
import comatching.comatching3.users.entity.Hobby;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.ContactFrequency;
import comatching.comatching3.users.enums.Gender;
import comatching.comatching3.users.enums.Role;
import comatching.comatching3.users.repository.HobbyRepository;
import comatching.comatching3.users.repository.UserAiFeatureRepository;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.EmailUtil;
import comatching.comatching3.util.RabbitMQ.CategoryRabbitMQUtil;
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
public class UserService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final UsersRepository usersRepository;
	private final UserAiFeatureRepository userAiFeatureRepository;
	private final HobbyRepository hobbyRepository;
	private final UniversityRepository universityRepository;
	private final SecurityUtil securityUtil;
	private final EmailUtil emailUtil;
	private final UniversityService universityService;
	private final PasswordEncoder passwordEncoder;
	private final SessionRepository<?> sessionRepository;
	private final CategoryRabbitMQUtil categoryRabbitMQUtil;

	public Long getParticipations() {
		return usersRepository.count();
	}

	public void userRegister(UserRegisterReq form) {
		if (usersRepository.existsByEmail(form.getAccountId())) {
			throw new BusinessException(ResponseCode.ACCOUNT_ID_DUPLICATED);
		}
		Users user = register(form);
		usersRepository.save(user);
	}

	private Users register(UserRegisterReq userInfo) {
		String encryptedPassword = passwordEncoder.encode(userInfo.getPassword());

		Users newUser = Users.builder()
			.socialId(UUIDUtil.generateSocialId())
			.provider("COMATCHING")
			.email(userInfo.getAccountId())
			.password(encryptedPassword)
			.role(Role.SOCIAL.getRoleName())
			.build();

		byte[] uuid = UUIDUtil.createUUID();
		UserAiFeature userAiFeature = UserAiFeature.builder()
			.users(newUser)
			.uuid(uuid)
			.build();

		newUser.updateUserAiFeature(userAiFeature);

		usersRepository.save(newUser);
		userAiFeatureRepository.save(userAiFeature);

		return newUser;
	}

	/**
	 * 소셜 유저 피처 정보 입력 후 유저로 역할 변경까지
	 *
	 * @param form social 유저의 Feature
	 */
	@Transactional
	public void inputUserInfo(UserFeatureReq form, HttpServletRequest request) {
		// 1) 사용자 정보 조회
		Users user = securityUtil.getCurrentUsersEntity();
		UserAiFeature userAiFeature = user.getUserAiFeature();

		// 2) 대학 정보 조회
		University university = getUniversityByName(form.getUniversity());

		// 3) 닉네임 필터 체크
		checkNicknameFilter(form.getUsername());

		// 4) 취미 부분 처리
		handleUserHobbies(userAiFeature, form.getHobby());

		// 5) UserAiFeature 업데이트
		updateUserAiFeature(userAiFeature, form);

		// 6) Users 엔티티 업데이트
		updateUsersEntity(user, form, university, userAiFeature, request);

		// todo: rabbitMQ 연결 후 주석 해제
		// Boolean isSuccess = userCrudRabbitMQUtil.sendUserChange(user.getUserAiFeature(), UserCrudType.CREATE);
		// if(!isSuccess){
		//     throw new BusinessException(ResponseCode.INPUT_FEATURE_FAIL);
		// }

	}

	/**
	 * 2) 대학 정보 조회
	 */
	private University getUniversityByName(String universityName) {
		return universityRepository.findByUniversityName(universityName)
			.orElseThrow(() -> new BusinessException(ResponseCode.SCHOOL_NOT_EXIST));
	}

	/**
	 * 3) 닉네임 필터 체크
	 */
	private void checkNicknameFilter(String nickname) {
		BadWordFiltering badWordFiltering = new BadWordFiltering();
		badWordFiltering.addAll(List.of(AdditionalBadWords.koreaWord2));

		if (badWordFiltering.check(nickname)) {
			throw new BusinessException(ResponseCode.INVALID_USERNAME);
		}
	}

	/**
	 * 4) Hobby 관련 로직
	 */
	private void handleUserHobbies(UserAiFeature userAiFeature, List<String> hobbyNames) {

		String raw = categoryRabbitMQUtil.classifyCategory(
			new CategoryReqMsg(hobbyNames, UUIDUtil.bytesToHex(userAiFeature.getUuid()))).getBigCategory().get(0);

		System.out.println("----------------" + raw + "--------------------");

		ObjectMapper mapper = new ObjectMapper();
		List<String> categories = null;
		try {
			categories = mapper.readValue(
				raw,
				new TypeReference<List<String>>() {}
			);
		} catch (JsonProcessingException e) {
			throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
		}

		List<Hobby> existingHobbies = hobbyRepository.findAllByUserAiFeature(userAiFeature);
		userAiFeature.removeHobby(existingHobbies);
		hobbyRepository.deleteAll(existingHobbies);

		List<String> finalCategories = categories;
		List<Hobby> newHobbyList = IntStream.range(0, hobbyNames.size())
			.mapToObj(i -> Hobby.builder()
				.hobbyName(hobbyNames.get(i))
				.userAiFeature(userAiFeature)
				.category(finalCategories.get(i))
				.build())
			.collect(Collectors.toList());
		hobbyRepository.saveAll(newHobbyList);

		userAiFeature.addHobby(newHobbyList);
	}

	/**
	 * 5) UserAiFeature 업데이트
	 */
	private void updateUserAiFeature(UserAiFeature userAiFeature, UserFeatureReq form) {
		int age = LocalDate.now().getYear() - Integer.parseInt(form.getYear()) + 1;
		userAiFeature.updateMajor(form.getMajor());
		userAiFeature.updateGender(Gender.fromAiValue(form.getGender()));
		userAiFeature.updateAge(age);
		userAiFeature.updateMbti(form.getMbti());
		userAiFeature.updateContactFrequency(ContactFrequency.fromAiValue(form.getContactFrequency()));

		userAiFeatureRepository.save(userAiFeature);
	}

	/**
	 * 6) Users 엔티티 업데이트
	 */
	private void updateUsersEntity(Users user, UserFeatureReq form, University university,
		UserAiFeature userAiFeature, HttpServletRequest request) {
		user.updateSong(form.getSong());
		user.updateComment(form.getComment());
		user.updateRole(Role.USER.getRoleName());
		user.updateUniversity(university);
		user.updateContactId(form.getContactId());
		user.updateUserAiFeature(userAiFeature);
		user.updateUsername(form.getUsername());
		user.updateBirthday(form.getYear() + "-" + form.getMonth() + "-" + form.getDay());

		usersRepository.save(user);

		securityUtil.setNewUserSecurityContext(user, request);
	}

	/**
	 * 연락처 중복확인(카톡, 인스타 id)
	 */
	public boolean isContactIdDuplicated(String contactId) {
		Users user = securityUtil.getCurrentUsersEntity();
		if (user.getContactId() != null && user.getContactId().equals(contactId)) {
			return false;
		}
		return usersRepository.existsByContactId(contactId);
	}

	/**
	 * 연락처 업데이트
	 */
	@Transactional
	public void updateContactId(String contactId) {
		Users user = securityUtil.getCurrentUsersEntity();
		user.updateContactId(contactId);
	}

	/**
	 * 유저 정보 업데이트
	 * todo: Hobby 대분류 관련 로직 추가
	 */
	@Transactional
	public void updateUserInfo(UserUpdateInfoReq form) {
		Users user = securityUtil.getCurrentUsersEntity();
		UserAiFeature userAiFeature = user.getUserAiFeature();

		if (form.getUniversity() != user.getUniversity().getUniversityName()) {
			University university = universityRepository.findByUniversityName(form.getUniversity())
				.orElseThrow(() -> new BusinessException(ResponseCode.SCHOOL_NOT_EXIST));

			user.updateUniversity(university);
		}

		if (form.getHobbies() != null) {
			List<String> categories = categoryRabbitMQUtil.classifyCategory(
				new CategoryReqMsg(form.getHobbies(), UUIDUtil.bytesToHex(userAiFeature.getUuid()))).getBigCategory();

			List<Hobby> existingHobbies = hobbyRepository.findAllByUserAiFeature(userAiFeature);
			userAiFeature.removeHobby(existingHobbies);
			hobbyRepository.deleteAll(existingHobbies);

			List<Hobby> newHobbyList = IntStream.range(0, form.getHobbies().size())
				.mapToObj(i -> Hobby.builder()
					.hobbyName(form.getHobbies().get(i))
					.userAiFeature(userAiFeature)
					.category(categories.get(i))
					.build())
				.collect(Collectors.toList());
			hobbyRepository.saveAll(newHobbyList);

			userAiFeature.addHobby(newHobbyList);
		}

		if (form.getUsername() != null) {
			user.updateUsername(form.getUsername());
		}

		if (form.getMajor() != null) {
			userAiFeature.updateMajor(form.getMajor());
		}

		if (form.getContactId() != null) {
			user.updateContactId(form.getContactId());
		}

		if (form.getMbti() != null) {
			userAiFeature.updateMbti(form.getMbti());
		}

		if (form.getContactFrequency() != null) {
			userAiFeature.updateContactFrequency(ContactFrequency.valueToAiValue(form.getContactFrequency()));
		}

		if (form.getSong() != null) {
			user.updateSong(form.getSong());
		}

		if (form.getComments() != null) {
			user.updateComment(form.getComments());
		}

	}

	public String getSchoolDomain(String universityName) {
		University university = universityRepository.findByUniversityName(universityName)
			.orElseThrow(() -> new BusinessException(ResponseCode.SCHOOL_NOT_EXIST));

		return university.getMailDomain();
	}

	/**
	 * 유저 학교 인증
	 */
	@Transactional
	public String userSchoolAuth(String schoolEmail) {

		// boolean isDuplicated = usersRepository.existsBySchoolEmail(schoolEmail);
		// if (isDuplicated) {
		// 	throw new BusinessException(ResponseCode.ARGUMENT_NOT_VALID);
		// }

		Users user = securityUtil.getCurrentUsersEntity();

		boolean checkEmailDomain = universityService.checkEmailDomain(schoolEmail,
			user.getUniversity().getUniversityName());
		if (!checkEmailDomain) {
			throw new BusinessException(ResponseCode.ARGUMENT_NOT_VALID);
		}
		user.updateSchoolEmail(schoolEmail);
		return sendVerifyEmail(schoolEmail);
	}

	/**
	 * 인증용 이메일 전송
	 */
	private String sendVerifyEmail(String email) {
		String verificationCode = String.valueOf(new Random().nextInt(900000) + 100000);
		String token = UUID.randomUUID().toString();
		String redisKey = "email-verification:" + token;

		redisTemplate.opsForValue().set(redisKey, verificationCode, 5, TimeUnit.MINUTES);
		emailUtil.sendEmail(email, "COMAtching 사용자 학교 인증 메일", "Your verification code is " + verificationCode);
		return token;
	}

	/**
	 * 이메일 인증번호 검사
	 */
	@Transactional
	public boolean verifyCode(EmailVerifyReq request) {
		String redisKey = "email-verification:" + request.getToken();
		String storedCode = (String)redisTemplate.opsForValue().get(redisKey);

		if (storedCode != null && storedCode.equals(request.getCode())) {
			Users user = securityUtil.getCurrentUsersEntity();
			user.schoolAuthenticationSuccess();

			redisTemplate.delete(redisKey);
			return true;
		}
		return false;
	}

	/**
	 * 유저 정보 조회
	 */
	@Transactional
	public UserInfoRes getUserInfo() {

		Users user = securityUtil.getCurrentUsersEntity();

		// if (user.getPickMe() <= 0) {
		// 	if (user.getPickMe() < 0) {
		// 		user.updatePickMe(0);
		// 		userCrudRabbitMQUtil.sendUserChange(user.getUserAiFeature(), UserCrudType.DELETE);
		// 	}
		// }

		List<String> hobbyResList = hobbyRepository.findAllByUserAiFeature(user.getUserAiFeature()).stream()
			.map(Hobby::getHobbyName)
			.toList();
		return UserInfoRes.builder()
			.username(user.getUsername())
			.age(user.getUserAiFeature().getAge())
			.university(user.getUniversity().getUniversityName())
			.major(user.getUserAiFeature().getMajor())
			.contactId(user.getContactId())
			.hobbies(hobbyResList)
			.mbti(user.getUserAiFeature().getMbti())
			.contactFrequency(user.getUserAiFeature().getContactFrequency())
			.song(user.getSong())
			.comment(user.getComment())
			.gender(user.getUserAiFeature().getGender())
			.schoolAuth(user.isSchoolAuth())
			.schoolEmail(user.getSchoolEmail())
			.build();
	}

	public UsernamePointRes getProfile() {

		Users user = securityUtil.getCurrentUsersEntity();

		return UsernamePointRes.builder()
			.socialId(user.getSocialId())
			.username(user.getUsername())
			.point(user.getPoint())
			.build();
	}

	/**
	 * 유저 포인트 조회
	 *
	 * @return 유저 포인트
	 */
	public Long getPoints() {
		Users user = securityUtil.getCurrentUsersEntity();
		return user.getPoint();
	}

	public CurrentPointRes inquiryCurrentPoint() {
		Users users = securityUtil.getCurrentUsersEntity();
		return new CurrentPointRes(users.getPoint());
	}

	/**
	 * 회원 탈퇴 요청
	 * 익명 사용자로 정보 변경
	 */
	@Transactional
	public void removeUser(HttpServletRequest request, HttpServletResponse response) {
		Users user = securityUtil.getCurrentUsersEntity();

		AnonymousUser anonymousUser = new AnonymousUser();

		user.updateUsername(anonymousUser.getUsername());
		user.updatePassword(anonymousUser.getPassword());
		user.updateEmail(anonymousUser.getEmail());
		user.updateRole(anonymousUser.getRole());
		user.updateSchoolEmail(anonymousUser.getSchoolEmail());
		user.updateContactId(anonymousUser.getContactId());
		user.subtractPoint(user.getPoint());
		user.subtractPayedPoint(user.getPayedPoint());

		HttpSession session = request.getSession(false);

		if (session != null) {
			String sessionId = session.getId();
			sessionRepository.deleteById(sessionId);
			session.invalidate();

			Cookie cookie = new Cookie("JSESSIONID", null);
			cookie.setPath("/");
			cookie.setHttpOnly(true);
			cookie.setMaxAge(0);
			response.addCookie(cookie);

			try {
				response.sendRedirect("https://comatching.site/");
			} catch (IOException e) {
				throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
			}

		} else {
			throw new BusinessException(ResponseCode.BAD_REQUEST);
		}
	}

}
