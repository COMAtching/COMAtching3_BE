package comatching.comatching3.users.entity;

import java.util.List;

import comatching.comatching3.users.enums.ContactFrequency;
import comatching.comatching3.users.enums.Gender;
import comatching.comatching3.users.enums.Hobby;
import comatching.comatching3.users.enums.Major;
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
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAiFeature {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_ai_feature_id")
	private Long id;


	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id")
	private Users users;

	private String mbti;

	@Enumerated(EnumType.STRING)
	private ContactFrequency contactFrequency;

	@Convert(converter = HobbyListConverter.class)
	private List<Hobby> hobby;

	private Integer age;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Enumerated(EnumType.STRING)
	private Major major;
}
