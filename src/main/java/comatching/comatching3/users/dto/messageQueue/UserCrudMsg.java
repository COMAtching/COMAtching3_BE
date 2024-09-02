package comatching.comatching3.users.dto.messageQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.enums.ContactFrequency;
import comatching.comatching3.users.enums.Gender;
import comatching.comatching3.users.enums.Hobby;
import comatching.comatching3.users.enums.UserCrudType;
import comatching.comatching3.util.UUIDUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCrudMsg {

	private UserCrudType type;
	private String uuid;
	private String mbti;
	private ContactFrequency contactFrequency;
	private String hobby;
	private Integer age;
	private Gender gender;
	private String major;

	public void updateFromUserAIFeatureAndType(UserCrudType type, UserAiFeature userAiFeature){
		this.type = type;
		this.uuid = UUIDUtil.bytesToHex(userAiFeature.getUuid());
		this.mbti = userAiFeature.getMbti();
		this.contactFrequency = userAiFeature.getContactFrequency();
		this.hobby = toHobbyString(userAiFeature.getHobby());
		this.age = userAiFeature.getAge();
		this.gender = userAiFeature.getGender();
		this.major = userAiFeature.getMajor();
	}

	private String toHobbyString(List<Hobby> hobbies){
		StringBuilder hobbyString = new StringBuilder();
		for(Hobby h : hobbies){
			hobbyString.append(h + ",");
		}

		return hobbyString.toString();
	}

	public List<Hobby> getHobbyAsList(){
		String hobbyString = this.hobby;
		if (hobbyString.endsWith(",")) {
			hobbyString = hobbyString.substring(0, hobbyString.length() - 1);
		}

		List<Hobby> hobbyList = new ArrayList<>();
		List<String> hobbies = Arrays.asList(hobbyString.split(","));

		for (String hobby : hobbies) {
			Hobby h = Hobby.from(hobby);
			if (h != null) {
				hobbyList.add(h);
			}
		}
		return hobbyList;
	}
}
