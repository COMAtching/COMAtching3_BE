package comatching.comatching3.admin.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicInfoRes {
	private String uuid;
	private String username;
	private String email;
	private String provider;
	private Long point;
	private Integer warnCount;
	private LocalDateTime registerAt;
}
