package comatching.comatching3.admin.controller;

import comatching.comatching3.admin.dto.request.DiscountEventRegisterReq;
import comatching.comatching3.admin.service.EventService;
import comatching.comatching3.util.Response;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/admin/event/register/discount")
    public Response<?> registerDiscountEvent(@Validated DiscountEventRegisterReq req){
        eventService.registerDiscountEvent(req);

        return Response.ok();

    }
}
