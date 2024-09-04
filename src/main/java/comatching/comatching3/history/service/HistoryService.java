package comatching.comatching3.history.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.history.dto.res.MatchHistoryRes;
import comatching.comatching3.history.entity.MatchingHistory;
import comatching.comatching3.history.repository.MatchingHistoryRepository;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {

	private final SecurityUtil securityUtil;
	private final MatchingHistoryRepository matchingHistoryRepository;

	public List<MatchHistoryRes> inquiryMatchHistory(){
		Users applier = securityUtil.getCurrentUsersEntity();

		List<MatchingHistory> matchingHistories = matchingHistoryRepository.findMatchingHistoriesByApplierId(applier.getId())
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
}
