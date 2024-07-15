package comatching.comatching3.history.entity;

import comatching.comatching3.users.entity.Users;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
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
	@JoinColumn(name = "match_history_id")
	private MatchingHistory matchingHistory;

	@Enumerated(EnumType.STRING)
	private PointHistoryType payType;

	private Integer pointAmount;

	private String tid;

	@Builder
	public PointHistory(Users users, MatchingHistory matchingHistory, PointHistoryType payType, Integer pointAmount, String tid) {
		this.users = users;
		this.matchingHistory = matchingHistory;
		this.payType = payType;
		this.pointAmount = pointAmount;
		this.tid = tid;
	}
}
