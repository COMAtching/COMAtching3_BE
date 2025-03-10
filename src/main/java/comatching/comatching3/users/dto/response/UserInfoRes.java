package comatching.comatching3.users.dto.response;

import java.util.List;

import comatching.comatching3.users.dto.response.HobbyRes;
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
	private String major;
	private Integer age;
	private String song;
	private String mbti;
	private String contactId;
	private Long point;
	private Integer pickMe;
	private Long participations;
	private List<HobbyRes> hobbies;
	private ContactFrequency contactFrequency;
	private Gender gender;
	private String comment;
	private Integer admissionYear;
	private boolean schoolAuth;
	private String schoolEmail;
	private Boolean event1;
}
