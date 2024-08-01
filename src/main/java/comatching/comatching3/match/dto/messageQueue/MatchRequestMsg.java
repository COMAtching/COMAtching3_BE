package comatching.comatching3.match.dto.messageQueue;

import comatching.comatching3.match.dto.request.MatchReq;
import lombok.Getter;

@Getter
public class MatchRequestMsg extends MatchReq {
	private String requestId;

	public MatchRequestMsg(MatchReq matchReq, String requestId){
		super.mbti = matchReq.getMbti();
		super.ageOption = matchReq.getAgeOption();
		super.uuid = matchReq.getUuid();
		super.hobbyOption = matchReq.getHobbyOption();
		super.contactFrequency = matchReq.getContactFrequency();
		super.sameMajorOption = matchReq.getSameMajorOption();
		this.requestId = requestId;
	}
}
