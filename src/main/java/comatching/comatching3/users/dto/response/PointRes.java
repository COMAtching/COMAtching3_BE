package comatching.comatching3.users.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointRes {

	String socialId;
	String username;
	String realName;
	Long point;
	int matchRemainCount;
}
