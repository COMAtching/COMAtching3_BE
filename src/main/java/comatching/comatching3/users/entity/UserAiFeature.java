package comatching.comatching3.users.entity;

import java.util.ArrayList;
import java.util.List;

import comatching.comatching3.users.enums.ContactFrequency;
import comatching.comatching3.users.enums.Gender;
import comatching.comatching3.users.enums.HobbyEnum;
import comatching.comatching3.util.HobbyListConverter;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
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

	@Column(columnDefinition = "BINARY(16)")
	private byte[] uuid;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id")
	private Users users;

	private String mbti;

	@Enumerated(EnumType.STRING)
	private ContactFrequency contactFrequency;

	@OneToMany(mappedBy = "userAiFeature", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Hobby> hobbyList = new ArrayList<>();

	@Min(20) @Max(30)
	private Integer age;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	private String  major;

	private Integer admissionYear;


	@Builder
	public UserAiFeature(byte[] uuid, Users users) {
		this.uuid = uuid;
		this.users = users;
	}

	public void updateMbti(String mbti) {
		this.mbti = mbti;
	}

	public void updateContactFrequency(ContactFrequency contactFrequency) {
		this.contactFrequency = contactFrequency;
	}

	public void updateHobby(List<Hobby> hobbies) {
		hobbyList.addAll(hobbies);
	}

	public void updateAge(Integer age) {
		this.age = age;
	}

	public void updateGender(Gender gender) {
		this.gender = gender;
	}

	public void updateMajor(String major) {
		this.major = major;
	}

	public void updateAdmissionYear(Integer admissionYear) {
		this.admissionYear = admissionYear;
	}

	public void updateUuid(byte[] uuid){
		this.uuid = uuid;
	}

}
