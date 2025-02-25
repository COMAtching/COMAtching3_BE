package comatching.comatching3.admin.dto.request;

import lombok.Getter;

@Getter
public class ChangeUserPointReq {

	private String uuid;
	private Long point;
	private String reason;
}
