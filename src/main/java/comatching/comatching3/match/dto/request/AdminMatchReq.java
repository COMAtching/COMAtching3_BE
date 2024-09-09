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
public class AdminMatchReq {
	@Pattern(regexp = "^[0-9a-fA-F]{32}$", message = "Invalid UUID format")
	private String code;

	@ValidEnum(enumClass = AgeOption.class)
	private AgeOption ageOption;

	private String mbtiOption;

	@ValidEnum(enumClass =  Hobby.class)
	private List<Hobby> hobbyOption;

	@ValidEnum(enumClass = ContactFrequencyOption.class)
	private ContactFrequencyOption contactFrequencyOption;

	private Boolean sameMajorOption;
}
