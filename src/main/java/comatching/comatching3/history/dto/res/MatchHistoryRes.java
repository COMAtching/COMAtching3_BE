package comatching.comatching3.history.dto.res;

import java.util.List;

import comatching.comatching3.users.entity.Hobby;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.ContactFrequency;
import comatching.comatching3.users.enums.Gender;
import comatching.comatching3.users.enums.HobbyEnum;
import lombok.Getter;

@Getter
public class MatchHistoryRes {

	private String mbti;
	private ContactFrequency contactFrequency;
	private List<Hobby> hobbyList;
	private Integer age;
	private Gender gender;
	private String  major;
	private Integer admissionYear;
	private String comment;
	private String song;
	private String contactId;

	public void updateFromUsers(Users users){
		UserAiFeature userAiFeature = users.getUserAiFeature();
		this.mbti = userAiFeature.getMbti();
		this.contactFrequency = userAiFeature.getContactFrequency();
		this.hobbyList = userAiFeature.getHobbyList();
		this.age = userAiFeature.getAge();
		this.gender = userAiFeature.getGender();
		this.major = userAiFeature.getMajor();
		this.admissionYear = userAiFeature.getAdmissionYear();
		this.comment = users.getComment();
		this.song = users.getSong();
		this.contactId = users.getContactId();
	}


}
