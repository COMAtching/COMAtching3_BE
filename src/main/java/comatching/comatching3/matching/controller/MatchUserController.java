package comatching.comatching3.matching.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import comatching.comatching3.matching.dto.request.MatchReq;
import comatching.comatching3.matching.dto.response.MatchRes;
import comatching.comatching3.matching.dto.response.RequestCodeRes;
import comatching.comatching3.matching.service.AuthCodeService;
import comatching.comatching3.matching.service.MatchService;
import comatching.comatching3.util.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth/user/api/match")
@RequiredArgsConstructor
public class MatchUserController {

	private final AuthCodeService authCodeService;
	private final MatchService matchService;

	@GetMapping("/request-code")
	public Response<RequestCodeRes> requestCode(){
		RequestCodeRes res = authCodeService.requestCode();
		return Response.ok(res);
	}

	@PostMapping("/request")
	public Response<MatchRes> requestMatch(@RequestBody @Valid MatchReq req){
		MatchRes res = matchService.requestMatch(req);
		return Response.ok(res);
	}

}
