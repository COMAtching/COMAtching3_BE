package comatching.comatching3.history.entity;

import java.util.List;

import comatching.comatching3.matching.dto.messageQueue.MatchRequestMsg;
import comatching.comatching3.matching.enums.AgeOption;
import comatching.comatching3.matching.enums.ContactFrequencyOption;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.enums.HobbyEnum;
import comatching.comatching3.util.BaseEntity;
import comatching.comatching3.util.HobbyListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingHistory extends BaseEntity {
	@Id
	@Column(name = "comatch_history_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "applier_info_id")
	private Users applier;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "enemy_info_id")
	private Users enemy;

	@Column(length = 4)
	private String mbtiOption;

	@Enumerated(value = EnumType.STRING)
	private AgeOption ageOption;

	@Enumerated(value = EnumType.STRING)
	private ContactFrequencyOption contactFrequencyOption;

	@Convert(converter = HobbyListConverter.class)
	private List<HobbyEnum> hobbyEnumOption;

	private Boolean noSameMajorOption;

	@OneToOne(mappedBy = "matchingHistory")
	@JoinColumn(name = "point_history_id")
	private PointHistory pointHistory;

	@Builder
	public MatchingHistory(Users applier, Users enemy, String mbtiOption, AgeOption ageOption,
		ContactFrequencyOption contactFrequencyOption, List<HobbyEnum> hobbyEnumOption, Boolean noSameMajorOption) {
		this.applier = applier;
		this.enemy = enemy;
		this.mbtiOption = mbtiOption;
		this.ageOption = ageOption;
		this.contactFrequencyOption = contactFrequencyOption;
		this.hobbyEnumOption = hobbyEnumOption;
		this.noSameMajorOption = noSameMajorOption;

	}

	public void updateOptionsFromRequestMsg(MatchRequestMsg matchRequestMsg) {
		this.noSameMajorOption = matchRequestMsg.getSameMajorOption();
		this.hobbyEnumOption = HobbyEnum.convertStringToHobbies(matchRequestMsg.getHobbyOption());
		this.ageOption = matchRequestMsg.getAgeOption();
		this.mbtiOption = matchRequestMsg.getMbtiOption();
		this.contactFrequencyOption = matchRequestMsg.getContactFrequencyOption();
	}

	@Override
	public String toString() {
		return "MatchingHistory{" +
			"id=" + id +
			", applier=" + (applier != null ? applier.getId() : "null") +
			", enemy=" + (enemy != null ? enemy.getId() : "null") +
			", mbtiOption='" + mbtiOption + '\'' +
			", ageOption=" + ageOption +
			", contactFrequencyOption=" + contactFrequencyOption +
			", hobbyOption=" + hobbyEnumOption +
			", noSameMajorOption=" + noSameMajorOption +
			'}';
	}

}
