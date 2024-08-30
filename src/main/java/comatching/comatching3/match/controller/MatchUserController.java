package comatching.comatching3.match.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import comatching.comatching3.match.dto.response.RequestCodeRes;
import comatching.comatching3.match.service.AuthCodeService;
import comatching.comatching3.util.Response;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/match/")
@RequiredArgsConstructor
public class MatchUserController {

	private final AuthCodeService authCodeService;

	@GetMapping("request-code")
	public Response<RequestCodeRes> requestCode(){
		RequestCodeRes res = authCodeService.requestCode();
		return Response.ok(res);
	}

}
