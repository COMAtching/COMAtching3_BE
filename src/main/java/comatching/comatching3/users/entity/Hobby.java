package comatching.comatching3.users.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hobby {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hobby_id")
	private Long id;

	private String category;

	private String hobbyName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_ai_feature_id")
	private UserAiFeature userAiFeature;

	@Builder
	public Hobby(String hobbyName, UserAiFeature userAiFeature, String category) {
		this.hobbyName = hobbyName;
		this.category = category;
		this.userAiFeature = userAiFeature;
	}

	public void setUserAiFeature(UserAiFeature userAiFeature) {
		this.userAiFeature = userAiFeature;
	}
}
