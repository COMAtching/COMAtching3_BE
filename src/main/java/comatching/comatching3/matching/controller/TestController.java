package comatching.comatching3.matching.controller;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.matching.service.TestService;
import comatching.comatching3.users.dto.messageQueue.CategoryReqMsg;
import comatching.comatching3.users.dto.messageQueue.CategoryResMsg;
import comatching.comatching3.util.RabbitMQ.CategoryRabbitMQUtil;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;
    private final CategoryRabbitMQUtil categoryRabbitMQUtil;

    @GetMapping("/admin/test/crud/request/create")
    public Response requestTestCreate() {

        testService.requestTestCrudCreate();


        return Response.ok();
    }

    @GetMapping("/admin/test/crud/request/delete")
    public Response requestTestDelete(@RequestParam Long userId) {
        testService.requestTestCrudDelete(userId);
        return Response.ok();
    }

    @GetMapping("/admin/test/crud/request/modify")
    public Response requestTestModify() {
        testService.requestTestCrudModify();
        return Response.ok();
    }

    @GetMapping("/admin/test/crud/request/match")
    public Response requestTestMatch() {
        testService.requestTestMatch();
        return Response.ok();
    }

    @GetMapping("/admin/test/category/test")
    public Response requestTestCategory() {
        CategoryReqMsg msg = new CategoryReqMsg(List.of("운동하기", "밥먹기"), "uuid1");

        CategoryResMsg res = categoryRabbitMQUtil.classifyCategory(msg);

        log.info(res.getStateCode());
        log.info(res.getBigCategory().toString());

        return Response.ok();
    }

    @GetMapping("/admin/teset/init/csv")
    public Response requestTestInitCsv() {
        try {
            testService.initCsv();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ResponseCode.BAD_REQUEST);
        }

        return Response.ok();
    }
}
