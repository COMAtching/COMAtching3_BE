package comatching.comatching3.history.entity;

import comatching.comatching3.history.enums.PointHistoryType;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id")
	private Users users;

//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "match_history_id")
//	private MatchingHistory matchingHistory;

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "admin_id")
//	private Admin approver;

	private PointHistoryType pointHistoryType;

	// 사용/소비한 포인트 양
	private Integer changeAmount;

	// 결과 픽미 횟수
	private Integer pickMe;

	// 결과적으로 남은 포인트
	private Integer totalPoint;

	@Builder
	public PointHistory(Users users, PointHistoryType pointHistoryType, Integer changeAmount, Integer pickMe, Integer totalPoint) {
		this.users = users;
		this.pointHistoryType = pointHistoryType;
		this.changeAmount = changeAmount;
		this.pickMe = pickMe;
		this.totalPoint = totalPoint;
	}

	public void setTotalPoint(Integer totalPoint) {
		this.totalPoint = totalPoint;
	}

	public void setPickMe(Integer pickMe) {
		this.pickMe = pickMe;
	}
}
