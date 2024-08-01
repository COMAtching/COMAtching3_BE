package comatching.comatching3.match.controller;

import org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import comatching.comatching3.match.dto.request.MatchReq;
import comatching.comatching3.match.dto.response.MatchRes;
import comatching.comatching3.match.service.MatchService;
import comatching.comatching3.util.Response;

@RestController
@RequestMapping("api/match/")
public class MatchUserController {

	private final MatchService matchService;

	MatchUserController(MatchService matchService){
		this.matchService = matchService;
	}



	@PostMapping("match-request")
	public Response<Void> requestMatch(@RequestBody MatchReq req){
		return matchService.requestMatch(req);
	}
}
