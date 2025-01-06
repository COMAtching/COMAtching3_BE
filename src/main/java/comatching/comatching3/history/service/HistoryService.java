package comatching.comatching3.history.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.history.dto.res.MatchHistoryRes;
import comatching.comatching3.history.dto.res.PointHistoryRes;
import comatching.comatching3.history.entity.MatchingHistory;
import comatching.comatching3.history.entity.PointHistory;
import comatching.comatching3.history.repository.MatchingHistoryRepository;
import comatching.comatching3.history.repository.PointHistoryRepository;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {

	private final SecurityUtil securityUtil;
	private final MatchingHistoryRepository matchingHistoryRepository;
	private final PointHistoryRepository pointHistoryRepository;

	public List<MatchHistoryRes> inquiryMatchHistory(){
		Users applier = securityUtil.getCurrentUsersEntity();

		List<MatchingHistory> matchingHistories = matchingHistoryRepository.findByApplierId(applier.getId())
			.orElseThrow(()->{
					throw new BusinessException(ResponseCode.MATCH_HISTORY_NOT_EXIST);
			});

		List<MatchHistoryRes> response = new ArrayList<>();

		for(MatchingHistory matchingHistory : matchingHistories){
			MatchHistoryRes res = new MatchHistoryRes();
			res.updateFromUsers(matchingHistory.getEnemy());
			response.add(res);
		}

		return response;
	}

	public List<PointHistoryRes> getAllPointHistory() {
		List<PointHistory> pointHistories = pointHistoryRepository.findAll();
		return convertToPointHistoryRes(pointHistories);
	}

	public List<PointHistoryRes> getAllPointHistory(byte[] uuid) {
		List<PointHistory> pointHistories = pointHistoryRepository.findAllByUuid(uuid);
		return convertToPointHistoryRes(pointHistories);
	}


	private List<PointHistoryRes> convertToPointHistoryRes(List<PointHistory> pointHistories) {
		return pointHistories.stream()
				.map(pointHistory -> PointHistoryRes.builder()
						.username(pointHistory.getUsers().getUsername())
						.pointHistoryType(pointHistory.getPointHistoryType())
						.changeAmount(pointHistory.getChangeAmount())
						.totalPoint(pointHistory.getTotalPoint())
						.pickMe(pointHistory.getPickMe())
						.timeStamp(pointHistory.getCreatedAt())
						.build())
				.collect(Collectors.toList());
	}

}
