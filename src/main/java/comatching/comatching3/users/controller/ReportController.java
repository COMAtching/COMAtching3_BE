package comatching.comatching3.users.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import comatching.comatching3.users.dto.request.ReportReq;
import comatching.comatching3.users.service.ReportService;
import comatching.comatching3.util.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	@PostMapping("/auth/user/report")
	public Response<Void> report(@RequestPart("form") ReportReq form,
		@RequestParam("reportImage") MultipartFile reportImage) {
		reportService.report(form, reportImage);
		return Response.ok();
	}
}
