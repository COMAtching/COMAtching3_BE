package comatching.comatching3.match.dto.messageQueue;

import comatching.comatching3.match.dto.request.MatchReq;
import lombok.Getter;

@Getter
public class MatchRequestMsg extends MatchReq {

	public MatchRequestMsg(MatchReq matchReq){
		this.mbti = matchReq.getMbti();
		this.ageOption = matchReq.getAgeOption();
		this.uuid = matchReq.getUuid();
		this.hobbyOption = matchReq.getHobbyOption();
		this.contactFrequencyOption = matchReq.getContactFrequencyOption();
		this.sameMajorOption = matchReq.getSameMajorOption();
	}
}
