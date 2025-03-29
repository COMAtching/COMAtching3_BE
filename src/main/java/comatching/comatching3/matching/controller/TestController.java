package comatching.comatching3.matching.controller;

import comatching.comatching3.matching.service.TestService;
import comatching.comatching3.util.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/admin/test/crud/request/create")
    public Response requestTestCreate() {

        testService.requestTestCrudCreate();


        return Response.ok();
    }

    @GetMapping("/admin/test/crud/request/delete")
    public Response requestTestDelete() {
        testService.requestTestCrudDelete();
        return Response.ok();
    }

    @GetMapping("/admin/test/crud/request/modify")
    public Response requestTestModify() {
        testService.requestTestCrudModify();
        return Response.ok();
    }
}
