package comatching.comatching3.match.dto.messageQueue;

import comatching.comatching3.match.dto.request.MatchReq;
import comatching.comatching3.match.enums.AgeOption;
import comatching.comatching3.match.enums.ContactFrequencyOption;
import comatching.comatching3.users.enums.Hobby;
import lombok.Getter;

@Getter
public class MatchRequestMsg{

	private String matcherUuid;
	private ContactFrequencyOption contactFrequencyOption;
	private String genderOption;
	private String hobbyOption;
	private Boolean sameMajorOption;
	private AgeOption ageOption;
	private String mbtiOption;
	private String myMajor;
	private Integer myAge;

	public void fromMatchReq(MatchReq matchReq){
		this.matcherUuid = matchReq.getUuid();
		this.contactFrequencyOption = matchReq.getContactFrequencyOption();
		this.genderOption = "FEMALE";
		this.hobbyOption = Hobby.convertHobbiesString(matchReq.getHobbyOption());
		this.sameMajorOption = matchReq.getSameMajorOption();
		this.ageOption = matchReq.getAgeOption();
		this.mbtiOption = matchReq.getMbti();
		this.myMajor = "정보통신전자공학부";
		this.myAge = 24;
	}
}
