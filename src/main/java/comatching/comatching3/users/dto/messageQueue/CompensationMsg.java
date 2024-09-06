package comatching.comatching3.users.dto.messageQueue;

import comatching.comatching3.users.enums.UserCrudType;
import lombok.Getter;

@Getter
public class CompensationMsg{
	private String errorCode;
	private String errorMessage;
	private UserCrudType requestType;
	private String userId;
}
