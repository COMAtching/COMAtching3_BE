package comatching.comatching3.users.dto.request;

import lombok.Getter;

@Getter
public class UserUpdateInfoReq {
	private String nickname;
	private int age;
	private String school;
	private String department;
	private String contact;
	private String[] interests;
	private String favoriteSong;
	private String selectMBTIEdit;
	private String contactFrequency;
	private String introduction;

}
