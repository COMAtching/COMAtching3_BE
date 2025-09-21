package comatching.comatching3.matching.dto.response;

import java.util.List;

import comatching.comatching3.users.entity.UserAiFeature;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilteredResult {

	List<UserAiFeature> filteredUsers;
	boolean refunded;
}
