package comatching.comatching3.users.dto.messageQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import comatching.comatching3.users.entity.Hobby;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.enums.HobbyEnum;
import comatching.comatching3.users.enums.UserCrudType;
import comatching.comatching3.util.UUIDUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCrudMsg {

	private UserCrudType type;
	private String uuid;
	private Integer age;
	private String contactFrequency;
	private String gender;
	private String hobby;
	private String major;
	private String mbti;


	public void updateFromUserAIFeatureAndType(UserCrudType type, UserAiFeature userAiFeature){
		this.type = type;
		this.uuid = UUIDUtil.bytesToHex(userAiFeature.getUuid());
		//this.uuid = "11ef6b568b0cdc2985f9c1ab3fa5c8fc";
		this.mbti = userAiFeature.getMbti();
		this.contactFrequency = userAiFeature.getContactFrequency().getAiValue();
		this.hobby = toHobbyString(userAiFeature.getHobbyList());
		this.age = userAiFeature.getAge();
		this.gender = userAiFeature.getGender().getAiValue();
		this.major = userAiFeature.getMajor();
	}

	private String toHobbyString(List<Hobby> hobbies){
		StringBuilder hobbyString = new StringBuilder();
		for(Hobby h : hobbies){
			hobbyString.append(h.getHobbyName() + ",");
		}

		return hobbyString.toString();
	}

	/*public List<HobbyEnum> getHobbyAsList(){
		String hobbyString = this.hobby;
		if (hobbyString.endsWith(",")) {
			hobbyString = hobbyString.substring(0, hobbyString.length() - 1);
		}

		List<HobbyEnum> hobbyEnumList = new ArrayList<>();
		List<String> hobbies = Arrays.asList(hobbyString.split(","));

		for (String hobby : hobbies) {
			HobbyEnum h = HobbyEnum.from(hobby);
			if (h != null) {
				hobbyEnumList.add(h);
			}
		}
		return hobbyEnumList;
	}*/
}
