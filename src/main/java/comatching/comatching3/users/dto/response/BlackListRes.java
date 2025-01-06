package comatching.comatching3.users.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlackListRes {
	private String uuid;
	private String username;
	private String role;
	private String reason;
	private String blackedAt;
}
