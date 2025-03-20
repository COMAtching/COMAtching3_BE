package comatching.comatching3.matching.dto.request;

import comatching.comatching3.matching.enums.AgeOption;
import comatching.comatching3.matching.enums.ContactFrequencyOption;
import comatching.comatching3.users.enums.HobbyEnum;
import comatching.comatching3.util.validation.ValidEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchReq {
    @ValidEnum(enumClass = AgeOption.class)
    private AgeOption ageOption;
    private String mbtiOption;

    @ValidEnum(enumClass = HobbyEnum.class)
    private ArrayList<HobbyEnum> hobbyEnumOption;

    @ValidEnum(enumClass = ContactFrequencyOption.class)
    private ContactFrequencyOption contactFrequencyOption;
    private Boolean sameMajorOption;
    private String duplicationList;
}
