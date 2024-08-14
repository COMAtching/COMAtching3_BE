package comatching.comatching3.users.dto.messageQueue;

import java.util.List;

import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.enums.ContactFrequency;
import comatching.comatching3.users.enums.Gender;
import comatching.comatching3.users.enums.Hobby;
import comatching.comatching3.users.enums.UserCrudType;
import comatching.comatching3.util.UUIDUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCrudMsg {

	private UserCrudType type;
	private String uuid;
	private String mbti;
	private ContactFrequency contactFrequency;
	private List<Hobby> hobby;
	private Integer age;
	private Gender gender;
	private String major;

	public static UserCrudMsg fromUserAIFeatureAndType(UserCrudType type, UserAiFeature userAiFeature){
		return UserCrudMsg.builder()
			.type(type)
			.uuid(UUIDUtil.bytesToHex(userAiFeature.getUuid()))
			.mbti(userAiFeature.getMbti())
			.contactFrequency(userAiFeature.getContactFrequency())
			.hobby(userAiFeature.getHobby())
			.gender(userAiFeature.getGender())
			.major(userAiFeature.getMajor())
			.build();
	}
}
