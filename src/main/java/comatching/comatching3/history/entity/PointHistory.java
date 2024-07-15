package comatching.comatching3.history.entity;

import comatching.comatching3.Users.entity.Users;
import comatching.comatching3.history.enums.PointHistoryType;
import comatching.comatching3.util.BaseEntity;
import jakarta.persistence.Column;
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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point_history")
public class PointHistory extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "point_history_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "users_id")
	private Users users;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comatch_history_id")
	private MatchingHistory matchingHistory;
	@Enumerated(EnumType.STRING)
	private PointHistoryType payType;
	private Integer beforeWork;
	private Integer afterWork;
	private Integer pointAmount;

	private Integer addPickMe;
}
