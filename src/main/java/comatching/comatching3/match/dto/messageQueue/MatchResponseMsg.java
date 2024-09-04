package comatching.comatching3.match.dto.messageQueue;

import lombok.Data;

@Data
public class MatchResponseMsg {
	private String errorMsg;
	private String errorMessage;
	private String enemyUuid;
}
