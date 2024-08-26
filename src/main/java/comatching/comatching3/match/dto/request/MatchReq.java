package comatching.comatching3.match.dto.request;

import java.util.ArrayList;
import java.util.List;

import comatching.comatching3.match.AgeOption;
import comatching.comatching3.match.ContactFrequencyOption;
import comatching.comatching3.users.enums.Hobby;
import comatching.comatching3.util.validation.ValidEnum;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchReq {
	@Pattern(regexp = "^[0-9a-fA-F]{32}$", message = "Invalid UUID format")
	protected String uuid;

	@ValidEnum(enumClass = AgeOption.class)
	protected AgeOption ageOption;
	protected String mbti;

	@ValidEnum(enumClass =  Hobby.class)
	protected List<Hobby> hobbyOption;

	@ValidEnum(enumClass = ContactFrequencyOption.class)
	protected ContactFrequencyOption contactFrequencyOption;
	protected Boolean sameMajorOption;
}
