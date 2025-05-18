package comatching.comatching3.chat.controller;

import comatching.comatching3.chat.domain.dto.ChatRoomReq;
import comatching.comatching3.chat.service.ChatService;
import comatching.comatching3.util.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth/user/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public Response createChatRoom(@RequestBody ChatRoomReq req) {

        log.info("id1={} id2={} chat", req.getId1(), req.getId2());
        chatService.createChatRoom(req.getId1(), req.getId2());
        return Response.ok();
    }
}