package comatching.comatching3.history.service;

import org.springframework.stereotype.Service;

import comatching.comatching3.history.entity.PointHistory;
import comatching.comatching3.history.enums.PointHistoryType;
import comatching.comatching3.history.repository.PointHistoryRepository;
import comatching.comatching3.users.entity.Users;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointHistoryService {

	private final PointHistoryRepository pointHistoryRepository;

	public void makePointHistory(Users user, PointHistoryType pointHistoryType, Long amount) {
		PointHistory pointHistory = PointHistory.builder()
			.users(user)
			.pointHistoryType(pointHistoryType)
			.changeAmount(amount)
			.pickMe(user.getPickMe())
			.totalPoint(user.getPoint())
			.build();

		pointHistoryRepository.save(pointHistory);
	}

	public void makePointHistoryWithReason(Users user, PointHistoryType pointHistoryType, Long amount, String reason) {
		PointHistory pointHistory = PointHistory.builder()
			.users(user)
			.pointHistoryType(pointHistoryType)
			.changeAmount(amount)
			.reason(reason)
			.pickMe(user.getPickMe())
			.totalPoint(user.getPoint())
			.build();

		pointHistoryRepository.save(pointHistory);
	}
}
