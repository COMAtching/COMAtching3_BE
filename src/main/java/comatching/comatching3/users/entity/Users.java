package comatching.comatching3.users.entity;

import java.util.ArrayList;
import java.util.List;

import comatching.comatching3.admin.entity.University;
import comatching.comatching3.history.entity.PointHistory;
import comatching.comatching3.match_message.entity.MessageMap;
import comatching.comatching3.util.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users", uniqueConstraints = {
	@UniqueConstraint(
		name = "social_id_unique",
		columnNames = "social_id"
	)
})
public class Users extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "users_id")
	private Long id;

	@OneToOne(mappedBy = "users", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private UserAiFeature userAiFeature;

	@OneToMany(mappedBy = "users", fetch = FetchType.LAZY)
	private List<PointHistory> pointHistoryList = new ArrayList<PointHistory>();

	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MessageMap> sentMessageMap = new ArrayList<MessageMap>();

	@OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MessageMap> receivedMessageMap = new ArrayList<MessageMap>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "university_id")
	private University university;

	private String socialId;

	private String username;

	private String email;

	private String role;

	private Integer pickMe = 0;

	private String song;

	private String comment;

	private Integer point = 0;


	@Builder
	public Users(String socialId, String role, String username) {
		this.socialId = socialId;
		this.role = role;
		this.username = username;
	}

	public void updateUserAiFeature(UserAiFeature userAiFeature) {
		this.userAiFeature = userAiFeature;
	}

	public void updateUniversity(University university) {
		this.university = university;
	}

	public void updateEmail(String email) {
		this.email = email;
	}

	public void updateRole(String role) {
		this.role = role;
	}

	public void updatePickMe(Integer pickMe) {
		this.pickMe = pickMe;
	}

	public void updateSong(String song) {
		this.song = song;
	}

	public void updateComment(String comment) {
		this.comment = comment;
	}

	public void updatePoint(Integer point) {
		this.point = point;
	}
}
