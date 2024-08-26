package comatching.comatching3.users.dto.messageQueue;

import lombok.Getter;

@Getter
public class CompensationMsg extends UserCrudMsg{
	private String errorCode;
	private String errorMessage;
}
