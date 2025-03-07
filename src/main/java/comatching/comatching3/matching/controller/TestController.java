package comatching.comatching3.matching.controller;

import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.enums.UserCrudType;
import comatching.comatching3.util.RabbitMQ.UserCrudRabbitMQUtil;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.UUIDUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final UserCrudRabbitMQUtil userCrudRabbitMQUtil;

    @GetMapping("/admin/test/rabbitMQ")
    public Response testRabbit() {

        UserAiFeature userAiFeature = new UserAiFeature(UUIDUtil.createUUID(), null);

        userCrudRabbitMQUtil.sendUserChange(userAiFeature, UserCrudType.CREATE);

        return Response.ok();
    }
}
