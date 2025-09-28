package comatching.comatching3.chat.controller;

import comatching.comatching3.chat.domain.dto.ChatResponse;
import comatching.comatching3.chat.domain.dto.ChatRoomInfoRes;
import comatching.comatching3.chat.domain.dto.ChatRoomReq;
import comatching.comatching3.chat.dto.ChatRoomListRes;
import comatching.comatching3.chat.service.ChatService;
import comatching.comatching3.util.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public Response<List<ChatRoomListRes>> getChatRoomList() {
        List<ChatRoomListRes> res = chatService.getChatRoomList();

        return Response.ok(res);
    }

    @GetMapping("/room")
    public Response<List<ChatResponse>> getChatRooms(@RequestParam Long roomId) {
        List<ChatResponse> res = chatService.getRoomChats(roomId);

        return Response.ok(res);
    }
}