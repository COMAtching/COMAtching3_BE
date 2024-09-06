package comatching.comatching3.match.dto.response;

import java.util.ArrayList;
import java.util.List;

import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.ContactFrequency;
import comatching.comatching3.users.enums.Gender;
import comatching.comatching3.users.enums.Hobby;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchRes {
	private String song;
	private String comment;
	private String mbti;
	private ContactFrequency contactFrequency;
	private List<Hobby> hobby;
	private Integer age;
	private Gender gender;
	private String major;
	private Integer currentPoint;
	private Integer admissionYear;
	private String contactId;

	public static MatchRes fromUsers(Users users){
        return MatchRes.builder()
			.song(users.getSong())
			.comment(users.getComment())
			.mbti(users.getUserAiFeature().getMbti())
			.contactFrequency(users.getUserAiFeature().getContactFrequency())
			.hobby(users.getUserAiFeature().getHobby())
			.age(users.getUserAiFeature().getAge())
			.major(users.getUserAiFeature().getMajor())
			.currentPoint(users.getPoint())
			.contactId(users.getContactId())
			.admissionYear(users.getUserAiFeature().getAdmissionYear())
			.build();
	}

	/**
	 * for test
	 * @return test result
	 */
	public static MatchRes testResult(){
		List<Hobby> hobbies = new ArrayList<>();
		hobbies.add(Hobby.게임);
		hobbies.add(Hobby.독서);

		return MatchRes.builder()
			.song("song1")
			.comment("hello")
			.mbti("TEST")
			.contactFrequency(ContactFrequency.NORMAL)
			.hobby(hobbies)
			.age(25)
			.major("컴퓨터정보공학부")
			.gender(Gender.FEMALE)
			.currentPoint(1500)
			.contactId("@instagramId")
			.build();
	}


}
