package comatching.comatching3.admin.controller;

import comatching.comatching3.admin.dto.request.UniversityReq;
import comatching.comatching3.admin.service.UniversityService;
import comatching.comatching3.util.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UniversityController {

    private final UniversityService universityService;

    /**
     * 대학 정보와 로고를 입력받아 등록하는 메소드
     * @param form 대학 정보
     * @param image 대학 로고
     * @return 필요한 정보 있으면 넣기
     */
    @PostMapping("/admin/api/university")
    public Response<?> createUniversity(@RequestPart("form") UniversityReq form,
                                        @RequestParam("universityImage") MultipartFile image) {

        universityService.createUniversity(form, image);
        return Response.ok();
    }
}
