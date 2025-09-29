package comatching.comatching3.users.dto.response;

import java.util.List;

import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.ContactFrequency;
import comatching.comatching3.users.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserInfoRes {
	private String username;
	private Integer age;
	private String university;
	private String major;
	private String contactId;
	private String contactType;
	private String song;
	private String mbti;
	private List<String> hobbies;
	private ContactFrequency contactFrequency;
	private Gender gender;
	private String comment;
	private boolean schoolAuth;
	private String schoolEmail;
	private int matchCount;

	public static UserInfoRes from(Users user, List<String> hobbies) {
		return UserInfoRes.builder()
			.username(user.getUsername())
			.age(user.getUserAiFeature().getAge())
			.university(user.getUniversity().getUniversityName())
			.major(user.getUserAiFeature().getMajor())
			.contactId(user.getContactId())
			.contactType(user.getContactType())
			.hobbies(hobbies)
			.mbti(user.getUserAiFeature().getMbti())
			.contactFrequency(user.getUserAiFeature().getContactFrequency())
			.song(user.getSong())
			.comment(user.getComment())
			.gender(user.getUserAiFeature().getGender())
			.schoolAuth(user.isSchoolAuth())
			.schoolEmail(user.getSchoolEmail())
			.matchCount(user.getMatchCount())
			.build();
	}
}
