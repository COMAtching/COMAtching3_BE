package comatching.comatching3.history.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import comatching.comatching3.history.dto.res.MatchHistoryRes;
import comatching.comatching3.history.service.HistoryService;
import comatching.comatching3.util.Response;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HistoryController {
	private final HistoryService historyService;

	@GetMapping("/auth/user/api/history/matching")
	public Response<List<MatchHistoryRes>> inquiryMatchHistory(){
		return Response.ok(historyService.inquiryMatchHistory());
	}
}

