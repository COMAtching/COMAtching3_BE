package comatching.comatching3.history.controller;

import java.util.List;

import comatching.comatching3.history.dto.res.PointHistoryRes;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import comatching.comatching3.history.dto.res.MatchHistoryRes;
import comatching.comatching3.history.service.HistoryService;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.service.UserService;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HistoryController {
	private final HistoryService historyService;
	private final SecurityUtil securityUtil;

	@GetMapping("/auth/user/api/history/matching")
	public Response<List<MatchHistoryRes>> inquiryMatchHistory(){
		return Response.ok(historyService.inquiryMatchHistory());
	}

	@GetMapping("/auth/admin/api/history/point")
	public Response<List<PointHistoryRes>> getAllPointHistory(@RequestParam(name = "uuid", required = false) String uuid) {

		if (uuid != null) {
			return Response.ok(historyService.getAllPointHistory(UUIDUtil.uuidStringToBytes(uuid)));
		}
		return Response.ok(historyService.getAllPointHistory());
	}

	@GetMapping("/auth/user/api/history/point")
	public Response<List<PointHistoryRes>> getAllPointHistory() {
		Users user = securityUtil.getCurrentUsersEntity();

		return Response.ok(historyService.getAllPointHistory(user.getUserAiFeature().getUuid()));
	}

}

