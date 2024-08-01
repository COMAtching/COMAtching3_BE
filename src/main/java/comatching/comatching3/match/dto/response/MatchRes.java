package comatching.comatching3.match.dto.response;

import java.util.List;

import comatching.comatching3.users.enums.ContactFrequency;
import comatching.comatching3.users.enums.Gender;
import comatching.comatching3.users.enums.Hobby;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchRes {
	private String song;
	private String word;
	private String mbti;
	private ContactFrequency contactFrequency;
	private List<Hobby> hobby;
	private Integer age;
	private Gender gender;
	private String major;
	private Integer currentPoint;
	private String enemyUsername;
	private String contactId;
}
