package comatching.comatching3.match.dto.cache;

import comatching.comatching3.match.enums.CheckStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CodeCheckInfo {
	private Long userId;
	private CheckStatus checkStatus;

	public void updateCheckStatus(CheckStatus checkStatus){
		this.checkStatus = checkStatus;
	}
}
