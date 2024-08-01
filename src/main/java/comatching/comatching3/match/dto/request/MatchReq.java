package comatching.comatching3.match.dto.request;

import comatching.comatching3.match.AgeOption;
import comatching.comatching3.users.enums.ContactFrequency;
import comatching.comatching3.users.enums.Hobby;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchReq {
	protected String uuid;
	protected AgeOption ageOption;
	protected String mbti;
	protected Hobby[] hobbyOption;
	protected ContactFrequency contactFrequency;
	protected Boolean sameMajorOption;
}
