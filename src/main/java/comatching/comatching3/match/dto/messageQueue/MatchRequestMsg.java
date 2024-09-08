package comatching.comatching3.match.dto.messageQueue;

import java.util.List;

import comatching.comatching3.history.entity.MatchingHistory;
import comatching.comatching3.match.dto.request.MatchReq;
import comatching.comatching3.match.enums.AgeOption;
import comatching.comatching3.match.enums.ContactFrequencyOption;
import comatching.comatching3.users.entity.UserAiFeature;
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

	public void fromMatchReq(MatchReq matchReq, UserAiFeature applierFeature){
		this.matcherUuid = UUIDUtil.bytesToHex(applierFeature.getUuid());
		this.contactFrequencyOption = matchReq.getContactFrequencyOption();
		this.genderOption = applierFeature.getGender().getAiValue();
		this.hobbyOption = Hobby.convertHobbiesString(matchReq.getHobbyOption());
		this.sameMajorOption = matchReq.getSameMajorOption();
		this.ageOption = matchReq.getAgeOption();
		this.mbtiOption = matchReq.getMbtiOption();
		this.myMajor = applierFeature.getMajor();
		this.myAge = applierFeature.getAge();
	}

	public void updateDuplicationListFromHistory(List<MatchingHistory> matchingHistories) {
		StringBuilder duplicationList = new StringBuilder();

		for (MatchingHistory history : matchingHistories) {
			String uuid = UUIDUtil.bytesToHex(history.getEnemy().getUserAiFeature().getUuid());
			duplicationList.append(uuid).append(",");
		}

		// 리스트가 비어 있지 않은 경우에만 마지막 쉼표를 제거
		if (duplicationList.length() > 0) {
			duplicationList.setLength(duplicationList.length() - 1);
		}

		this.duplicationList = duplicationList.toString();
	}

	public void updateNoDuplication(){
		this.duplicationList = "";
	}

	public void updateDuplicationList(String duplicationList){
		this.duplicationList = duplicationList;
	}
}
