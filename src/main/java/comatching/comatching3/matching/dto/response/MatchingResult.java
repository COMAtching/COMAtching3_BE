package comatching.comatching3.matching.dto.response;

import comatching.comatching3.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingResult {
	private Users enemyUser;
	private boolean refunded;
}
