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
import comatching.comatching3.util.HobbyCategoryUtil;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoAiMatchingService {

	private final SecurityUtil securityUtil;
	private final UserAiFeatureRepository userAiFeatureRepository;
	private final MatchingHistoryRepository matchingHistoryRepository;

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

		List<UserAiFeature> result = enemyList;
		boolean refunded = false;

		if (importantOption != null && !importantOption.equals("UNSELECTED")) {
			switch (importantOption) {
				case "ageOption":
					result = checkAge(result, matchReq.getAgeOption(), age, true);
					refunded = checkAndReturnImportantOptionPay(result.size(), applier);
					break;
				case "mbtiOption":
					result = checkMbti(result, matchReq.getMbtiOption(), true);
					refunded = checkAndReturnImportantOptionPay(result.size(), applier);
					break;
				case "hobbyOption":
					result = checkHobby(result,
						matchReq.getHobbyOption().get(0).getValue(), true);
					refunded = checkAndReturnImportantOptionPay(result.size(), applier);
					break;
				case "contactFrequencyOption":
					result = checkContactFrequency(result,
						matchReq.getContactFrequencyOption(), true);
					refunded = checkAndReturnImportantOptionPay(result.size(), applier);
					break;
			}
		}

		if (!"ageOption".equals(importantOption)) {
			result = checkAge(result, matchReq.getAgeOption(), age, false);
		}

		if (!"mbtiOption".equals(importantOption)) {
			result = checkMbti(result, matchReq.getMbtiOption(), false);
		}

		if (!"hobbyOption".equals(importantOption)) {
			result = checkHobby(result, matchReq.getHobbyOption().get(0).getValue(), false);
		}

		if (!"contactFrequencyOption".equals(importantOption)) {
			result = checkContactFrequency(result, matchReq.getContactFrequencyOption(), false);
		}

		return FilteredResult.builder()
			.filteredUsers(result)
			.refunded(refunded)
			.build();
	}

	private List<UserAiFeature> getUserList(Gender gender, String major, Users applier, Long usePoint) {
		List<UserAiFeature> result = null;

		if (major != null) {
			result = userAiFeatureRepository.findAllByGenderAndMajorNotWithHobbiesExcludingPreviousMatches(gender,
				major, applier.getId());
		} else {
			result = userAiFeatureRepository.findAllByGenderWithHobbiesExcludingPreviousMatches(gender,
				applier.getId());
		}

		if (result.isEmpty() || result == null) {
			applier.addPoint(usePoint);
			throw new BusinessException(ResponseCode.NO_ENEMY_AVAILABLE);
		}

		return result;
	}

	private List<UserAiFeature> checkAge(List<UserAiFeature> enemyList, AgeOption ageOption, Integer baseAge,
		boolean isImportant) {
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

		return filtered.size() >= minSize ? filtered : enemyList;
	}

	private List<UserAiFeature> checkContactFrequency(List<UserAiFeature> enemyList, ContactFrequencyOption option,
		boolean isImportant) {
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
		return filtered.size() >= minSize ? filtered : enemyList;
	}

	private List<UserAiFeature> checkHobby(List<UserAiFeature> enemyList, String hobbyOption, boolean isImportant) {

		if (hobbyOption == null || hobbyOption.isBlank() || !HobbyCategoryUtil.isValidCategory(hobbyOption)) {
			return enemyList;
		}

		String targetCategory = HobbyCategoryUtil.getCategory(hobbyOption);

		List<UserAiFeature> filtered = enemyList.stream()
			.filter(user -> {
				List<Hobby> hobbies = user.getHobbyList();
				if (hobbies == null || hobbies.isEmpty())
					return false;

				return hobbies.stream()
					.map(Hobby::getCategory)
					.anyMatch(category -> category.equals(targetCategory));
			})
			.toList();

		int minSize = isImportant ? 1 : 3;
		return filtered.size() >= minSize ? filtered : enemyList;
	}

	private List<UserAiFeature> checkMbti(List<UserAiFeature> enemyList, String mbtiOptions, boolean isImportant) {
		if (mbtiOptions == null || mbtiOptions.isBlank() || mbtiOptions.length() != 2) {
			return enemyList;
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
		return filtered.size() >= minSize ? filtered : enemyList;
	}

	private boolean checkAndReturnImportantOptionPay(int enemyListSize, Users applier) {
		if (enemyListSize < 1) {
			applier.addPoint(300L);
			return true;
		}
		return false;
	}

}
