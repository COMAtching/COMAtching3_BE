package comatching.comatching3.matching.dto.response;

import comatching.comatching3.chat.domain.ChatRole;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.ContactFrequency;
import comatching.comatching3.users.enums.Gender;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MatchRes {
	private String song;
	private String comment;
	private String mbti;
	private ContactFrequency contactFrequency;
	private List<String> hobbyList;
	private Integer age;
	private Gender gender;
	private String major;
	private Long currentPoint;
	private String contactId;
	private String contactType;
	private ChatRole myChaRole;
	private Long chatRoomId;
	private boolean refunded;

	public static MatchRes fromUsers(Users users) {
		return MatchRes.builder()
			.song(users.getSong())
			.comment(users.getComment())
			.mbti(users.getUserAiFeature().getMbti())
			.contactFrequency(users.getUserAiFeature().getContactFrequency())
			.hobbyList(users.getUserAiFeature().getHobbyNameList())
			.age(users.getUserAiFeature().getAge())
			.major(users.getUserAiFeature().getMajor())
			.contactId(users.getContactId())
			.contactType(users.getContactType())
			.gender(users.getUserAiFeature().getGender())
			.build();
	}

	public void updateChatRoom(Long chatRoomId) {
		this.chatRoomId = chatRoomId;
		this.myChaRole = ChatRole.PICKER;
	}

	public void updateCurrentPoint(Long point) {
		this.currentPoint = point;
	}

	public void updateRefunded(boolean refunded) {
		this.refunded = refunded;
	}
}
