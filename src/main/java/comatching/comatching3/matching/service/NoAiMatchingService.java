package comatching.comatching3.matching.service;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.history.entity.MatchingHistory;
import comatching.comatching3.history.repository.MatchingHistoryRepository;
import comatching.comatching3.matching.dto.request.MatchReq;
import comatching.comatching3.matching.dto.response.FilteredResult;
import comatching.comatching3.matching.dto.response.MatchingResult;
import comatching.comatching3.matching.enums.AgeOption;
import comatching.comatching3.matching.enums.ContactFrequencyOption;
import comatching.comatching3.users.entity.Hobby;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.ContactFrequency;
import comatching.comatching3.users.enums.Gender;
import comatching.comatching3.users.repository.UserAiFeatureRepository;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoAiMatchingService {

	private final SecurityUtil securityUtil;
	private final UserAiFeatureRepository userAiFeatureRepository;
	private final MatchingHistoryRepository matchingHistoryRepository;

	private static final Map<String, List<String>> HOBBY_CATEGORY_MAP = Map.of(
		"스포츠", List.of("헬스", "수영", "러닝", "축구", "농구", "야구", "배드민턴", "테니스", "클라이밍", "복싱", "골프"),
		"문화예술",
		List.of("인디음악", "락", "랩/힙합", "발라드", "RnB", "팝송", "K-팝", "클래식", "독서", "영화", "패션", "전시", "공예", "뮤지컬", "사진", "뷰티",
			"커피", "술/와인", "페스티벌"),
		"악기", List.of("피아노", "기타", "바이올린", "드럼", "플룻"),
		"여행", List.of("국내여행", "해외여행", "맛집탐방", "캠핑", "등산", "드라이브"),
		"일상/공부", List.of("공부", "테크", "사랑", "시사/정치", "취업", "철학", "연구", "천문학", "과학", "시/문학"),
		"게임", List.of("배틀그라운드", "메이플스토리", "오버워치", "발로란트", "피파", "롤")
	);

	public MatchingResult noAiMatching(MatchReq matchReq, Long usePoint) {

		Users currentUser = securityUtil.getCurrentUsersEntity();
		UserAiFeature currentUserAiFeature = currentUser.getUserAiFeature();

		boolean refunded = false;
		String importantOption = matchReq.getImportantOption();

		Gender gender = currentUserAiFeature.getGender();
		String major = currentUserAiFeature.getMajor();
		Integer age = currentUserAiFeature.getAge();

		List<UserAiFeature> enemyList = getUserList(gender, matchReq.getSameMajorOption() ? major : null, currentUser,
			usePoint);

		FilteredResult filteredResult = applyFiltersInOrder(enemyList, matchReq, importantOption, age, currentUser);
		List<UserAiFeature> result = filteredResult.getFilteredUsers();
		refunded = filteredResult.isRefunded();

		Users enemyUser = null;
		if (!result.isEmpty()) {
			Random random = new Random();
			UserAiFeature selectedUserFeature = result.get(random.nextInt(result.size()));
			enemyUser = selectedUserFeature.getUsers();
		}

		return MatchingResult.builder()
			.enemyUser(enemyUser)
			.refunded(refunded)
			.build();
	}

	public void createHistory(MatchReq matchReq, Users applier, Users enemy) {
		MatchingHistory history = MatchingHistory.builder()
			.enemy(enemy)
			.applier(applier)
			.build();

		history.updateOptionsFromRequestMsg(matchReq);
		matchingHistoryRepository.save(history);
	}

	private FilteredResult applyFiltersInOrder(List<UserAiFeature> enemyList, MatchReq matchReq, String importantOption,
		Integer age, Users applier) {

		FilteredResult filteredResult = new FilteredResult(enemyList, false);

		if (importantOption != null && !importantOption.equals("UNSELECTED")) {
			switch (importantOption) {
				case "ageOption":
					filteredResult = checkAge(filteredResult.getFilteredUsers(), matchReq.getAgeOption(), age, true,
						applier,
						filteredResult.isRefunded());
					break;
				case "mbtiOption":
					filteredResult = checkMbti(filteredResult.getFilteredUsers(), matchReq.getMbtiOption(), true,
						applier, filteredResult.isRefunded());
					break;
				case "hobbyOption":
					filteredResult = checkHobby(filteredResult.getFilteredUsers(),
						matchReq.getHobbyOption().get(0).getValue(), true, applier, filteredResult.isRefunded());
					break;
				case "contactFrequencyOption":
					filteredResult = checkContactFrequency(filteredResult.getFilteredUsers(),
						matchReq.getContactFrequencyOption(), true, applier, filteredResult.isRefunded());
					break;
			}
		}

		if (!"ageOption".equals(importantOption)) {
			filteredResult = checkAge(filteredResult.getFilteredUsers(), matchReq.getAgeOption(), age, false, applier,
				filteredResult.isRefunded());
		}

		if (!"mbtiOption".equals(importantOption)) {
			filteredResult = checkMbti(filteredResult.getFilteredUsers(), matchReq.getMbtiOption(), false, applier,
				filteredResult.isRefunded());
		}

		if (!"hobbyOption".equals(importantOption)) {
			filteredResult = checkHobby(filteredResult.getFilteredUsers(), matchReq.getHobbyOption().get(0).getValue(),
				false, applier, filteredResult.isRefunded());
		}

		if (!"contactFrequencyOption".equals(importantOption)) {
			filteredResult = checkContactFrequency(filteredResult.getFilteredUsers(),
				matchReq.getContactFrequencyOption(), false, applier, filteredResult.isRefunded());
		}

		return filteredResult;
	}

	private List<UserAiFeature> getUserList(Gender gender, String major, Users applier, Long usePoint) {
		List<UserAiFeature> result;

		if (major != null) {
			result = userAiFeatureRepository.findAllByGenderAndMajorNotWithHobbiesExcludingPreviousMatches(gender,
				major, applier.getId());
		} else {
			result = userAiFeatureRepository.findAllByGenderWithHobbiesExcludingPreviousMatches(gender,
				applier.getId());
		}

		if (result.isEmpty()) {
			applier.addPoint(usePoint);
			throw new BusinessException(ResponseCode.NO_ENEMY_AVAILABLE);
		}

		return result;
	}

	private FilteredResult checkAge(List<UserAiFeature> enemyList, AgeOption ageOption, Integer baseAge,
		boolean isImportant, Users applier, boolean refunded) {
		List<UserAiFeature> filtered = enemyList.stream()
			.filter(user -> {
				Integer age = user.getAge();

				if (age == null)
					return false;

				return switch (ageOption) {
					case YOUNGER -> age < baseAge;
					case EQUAL -> age.equals(baseAge);
					case OLDER -> age > baseAge;
					case UNSELECTED -> true;
				};
			})
			.toList();

		int minSize = isImportant ? 1 : 3;
		boolean isRefunded = checkAndReturnImportantOptionPay(filtered.size(), minSize, applier);
		refunded = refunded || isRefunded;

		return FilteredResult.builder()
			.filteredUsers(filtered.size() >= minSize ? filtered : enemyList)
			.refunded(refunded)
			.build();
	}

	private FilteredResult checkContactFrequency(List<UserAiFeature> enemyList, ContactFrequencyOption option,
		boolean isImportant, Users applier, boolean refunded) {
		List<UserAiFeature> filtered = enemyList.stream()
			.filter(user -> {
				ContactFrequency cf = user.getContactFrequency();
				if (cf == null)
					return false;

				return switch (option) {
					case FREQUENT -> cf == ContactFrequency.FREQUENT;
					case NORMAL -> cf == ContactFrequency.NORMAL;
					case NOT_FREQUENT -> cf == ContactFrequency.NOT_FREQUENT;
					case UNSELECTED -> true;
				};
			})
			.toList();

		int minSize = isImportant ? 1 : 3;
		boolean isRefunded = checkAndReturnImportantOptionPay(filtered.size(), minSize, applier);
		refunded = refunded || isRefunded;

		return FilteredResult.builder()
			.filteredUsers(filtered.size() >= minSize ? filtered : enemyList)
			.refunded(refunded)
			.build();
	}

	private FilteredResult checkHobby(List<UserAiFeature> enemyList, String hobbyOption, boolean isImportant,
		Users applier, boolean refunded) {

		if (hobbyOption == null || hobbyOption.isBlank() || !HOBBY_CATEGORY_MAP.containsKey(hobbyOption)) {
			return FilteredResult.builder()
				.filteredUsers(enemyList)
				.refunded(false)
				.build();
		}

		List<String> targetSubCategories = HOBBY_CATEGORY_MAP.get(hobbyOption);

		List<UserAiFeature> filtered = enemyList.stream()
			.filter(user -> {
				List<Hobby> hobbies = user.getHobbyList();
				if (hobbies == null || hobbies.isEmpty())
					return false;

				return hobbies.stream()
					.map(Hobby::getCategory)
					.anyMatch(targetSubCategories::contains);
			})
			.toList();

		int minSize = isImportant ? 1 : 3;
		boolean isRefunded = checkAndReturnImportantOptionPay(filtered.size(), minSize, applier);
		refunded = refunded || isRefunded;

		return FilteredResult.builder()
			.filteredUsers(filtered.size() >= minSize ? filtered : enemyList)
			.refunded(refunded)
			.build();
	}

	private FilteredResult checkMbti(List<UserAiFeature> enemyList, String mbtiOptions, boolean isImportant,
		Users applier, boolean refunded) {
		if (mbtiOptions == null || mbtiOptions.isBlank() || mbtiOptions.length() != 2) {
			return FilteredResult.builder()
				.filteredUsers(enemyList)
				.refunded(false)
				.build();
		}

		String upperOptions = mbtiOptions.toUpperCase();
		char firstOption = upperOptions.charAt(0);
		char secondOption = upperOptions.charAt(1);

		List<UserAiFeature> filtered = enemyList.stream()
			.filter(user -> {
				String userMbti = user.getMbti();
				if (userMbti == null)
					return false;

				String upperUserMbti = userMbti.toUpperCase();

				return upperUserMbti.indexOf(firstOption) >= 0 && upperUserMbti.indexOf(secondOption) >= 0;
			})
			.toList();

		int minSize = isImportant ? 1 : 3;
		boolean isRefunded = checkAndReturnImportantOptionPay(filtered.size(), minSize, applier);
		refunded = refunded || isRefunded;

		return FilteredResult.builder()
			.filteredUsers(filtered.size() >= minSize ? filtered : enemyList)
			.refunded(refunded)
			.build();
	}

	private boolean checkAndReturnImportantOptionPay(int enemyListSize, int minSize, Users applier) {
		if (enemyListSize < minSize) {
			applier.addPoint(300L);
			return true;
		}
		return false;
	}

}
