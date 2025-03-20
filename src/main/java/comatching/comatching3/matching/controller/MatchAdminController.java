package comatching.comatching3.matching.controller;

import comatching.comatching3.matching.dto.request.CodeCheckReq;
import comatching.comatching3.matching.dto.response.CodeCheckRes;
import comatching.comatching3.matching.service.AuthCodeService;
import comatching.comatching3.matching.service.MatchService;
import comatching.comatching3.util.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/admin/api/match/")
@RequiredArgsConstructor
public class MatchAdminController {

    private final MatchService matchService;
    private final AuthCodeService authCodeService;

    @PostMapping("/check-code")
    public Response<CodeCheckRes> checkCode(@RequestBody CodeCheckReq req) {
        CodeCheckRes res = authCodeService.checkCode(req);
        return Response.ok(res);
    }

	/*@PostMapping("/request")
	public Response<MatchRes> requestMatch(@RequestBody @Valid AdminMatchReq req){
		MatchRes res = matchService.requestAdminMatch(req);
		return Response.ok(res);
	}*/
}
