package comatching.comatching3.match.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import comatching.comatching3.match.dto.request.CodeCheckReq;
import comatching.comatching3.match.dto.request.MatchReq;
import comatching.comatching3.match.dto.response.CodeCheckRes;
import comatching.comatching3.match.dto.response.MatchRes;
import comatching.comatching3.match.service.AuthCodeService;
import comatching.comatching3.match.service.MatchService;
import comatching.comatching3.users.dto.UserFeatureReq;
import comatching.comatching3.util.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchAdminController {

	private final MatchService matchService;
	private final AuthCodeService authCodeService;

	@PostMapping("match-request")
	public Response<MatchRes> requestMatch(@RequestBody @Valid MatchReq req){
		MatchRes res = matchService.requestMatch(req);
		//return Response.ok(res);
		return Response.ok(MatchRes.testResult());
	}

	@PostMapping("test/crud")
	public Response<Void> requestCrud(@RequestBody UserFeatureReq req){
		//System.out.println(req.getGender());
		matchService.testCrud(req);
		return Response.ok();
	}

	@PostMapping("check-code")
	public Response<CodeCheckRes> checkCode(CodeCheckReq req){
		CodeCheckRes res = authCodeService.checkCode(req);
		return Response.ok(res);

	}
}
