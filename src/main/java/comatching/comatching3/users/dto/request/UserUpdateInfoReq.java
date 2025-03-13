package comatching.comatching3.users.dto.request;

import java.util.List;

import lombok.Getter;

@Getter
public class UserUpdateInfoReq {
	private String username;
	private String university;
	private String major;
	private String contactId;
	private List<String> hobbies;
	private String song;
	private String mbti;
	private String contactFrequency;
	private String comments;
}
