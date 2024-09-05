package comatching.comatching3.match.dto.messageQueue;

import java.util.List;

import comatching.comatching3.history.entity.MatchingHistory;
import comatching.comatching3.match.dto.request.MatchReq;
import comatching.comatching3.match.enums.AgeOption;
import comatching.comatching3.match.enums.ContactFrequencyOption;
import comatching.comatching3.users.enums.Hobby;
import comatching.comatching3.util.UUIDUtil;
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
	private String duplicationList;

	public void fromMatchReq(MatchReq matchReq){
		this.matcherUuid = matchReq.getUuid();
		this.contactFrequencyOption = matchReq.getContactFrequencyOption();
		this.genderOption = "FEMALE";
		this.hobbyOption = Hobby.convertHobbiesString(matchReq.getHobbyOption());
		this.sameMajorOption = matchReq.getSameMajorOption();
		this.ageOption = matchReq.getAgeOption();
		this.mbtiOption = matchReq.getMbtiOption();
		this.myMajor = "정보통신전자공학부";
		this.myAge = 24;
	}

	public void updateDuplicationListFromHistory(List<MatchingHistory> matchingHistories){
		StringBuilder duplicationList = new StringBuilder();
		for(MatchingHistory history : matchingHistories){
			String uuid = UUIDUtil.bytesToHex(history.getEnemy().getUserAiFeature().getUuid());
			duplicationList.append(uuid + ",");
		}
		duplicationList.deleteCharAt(duplicationList.length());
		this.duplicationList = duplicationList.toString();
	}

	public void updateNoDuplication(){
		this.duplicationList = "";
	}

	public void updateDuplicationList(String duplicationList){
		this.duplicationList = duplicationList;
	}
}
