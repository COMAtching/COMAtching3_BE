package comatching.comatching3.match.dto.request;

import java.util.List;

import comatching.comatching3.match.enums.AgeOption;
import comatching.comatching3.match.enums.ContactFrequencyOption;
import comatching.comatching3.users.enums.Hobby;
import comatching.comatching3.util.validation.ValidEnum;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchReq {
	@Pattern(regexp = "^[0-9a-fA-F]{32}$", message = "Invalid UUID format")
	protected String uuid;

	@ValidEnum(enumClass = AgeOption.class)
	protected AgeOption ageOption;
	protected String mbtiOption;

	@ValidEnum(enumClass =  Hobby.class)
	protected List<Hobby> hobbyOption;

	@ValidEnum(enumClass = ContactFrequencyOption.class)
	protected ContactFrequencyOption contactFrequencyOption;
	protected Boolean sameMajorOption;
	protected String duplicationList;
}
