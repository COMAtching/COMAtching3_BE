package comatching.comatching3.users.dto.request;

import lombok.Getter;

@Getter
public class ReportReq {
	private String reportedUserSocialId;
	private String reportCategory;
	private String reportContent;

}
