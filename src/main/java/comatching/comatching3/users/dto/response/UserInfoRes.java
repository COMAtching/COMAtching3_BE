package comatching.comatching3.users.dto.response;

import java.util.List;

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
}
