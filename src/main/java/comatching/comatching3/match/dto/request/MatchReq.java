package comatching.comatching3.match.dto.request;

import java.util.List;

import comatching.comatching3.match.enums.AgeOption;
import comatching.comatching3.match.enums.ContactFrequencyOption;
import comatching.comatching3.users.enums.HobbyEnum;
import comatching.comatching3.util.validation.ValidEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchReq {
	@ValidEnum(enumClass = AgeOption.class)
	private AgeOption ageOption;
	private String mbtiOption;

	@ValidEnum(enumClass =  HobbyEnum.class)
	private List<HobbyEnum> hobbyEnumOption;

	@ValidEnum(enumClass = ContactFrequencyOption.class)
	private ContactFrequencyOption contactFrequencyOption;
	private Boolean sameMajorOption;
	private String duplicationList;
}
