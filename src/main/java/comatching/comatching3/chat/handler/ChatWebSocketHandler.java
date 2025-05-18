package comatching.comatching3.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import comatching.comatching3.chat.SocketSecurityUtil;
import comatching.comatching3.chat.domain.dto.ChatMessageDto;
import comatching.comatching3.chat.service.ChatService;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final UsersRepository usersRepository;

    // 채팅방 ID 기준으로 세션들을 관리
    private static final Map<Long, List<WebSocketSession>> chatRoomSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long chatRoomId = getChatRoomId(session);
        log.info("chatRoomId: {}", chatRoomId);
        chatRoomSessions.computeIfAbsent(chatRoomId, k -> new CopyOnWriteArrayList<>()).add(session);

        Users user = SocketSecurityUtil.getCurrentUsersEntity(session, usersRepository);
        log.info("웹소켓 연결됨 - 사용자: {}, 채팅방: {}", user.getUsername(), chatRoomId);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        ChatMessageDto chatMessageDto = objectMapper.readValue(message.getPayload().toString(), ChatMessageDto.class);
        Long chatRoomId = chatMessageDto.getChatRoomId();

        Users sender = SocketSecurityUtil.getCurrentUsersEntity(session, usersRepository);
        Response responseDto = chatService.saveChatMessage(sender, chatMessageDto.getContent(), chatRoomId);

        String response = objectMapper.writeValueAsString(responseDto);
        List<WebSocketSession> sessions = chatRoomSessions.getOrDefault(chatRoomId, List.of());

        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(response));
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("웹소켓 에러 발생: {}", exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        chatRoomSessions.values().forEach(list -> list.remove(session));
        log.info("웹소켓 연결 종료");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private Long getChatRoomId(WebSocketSession session) {
        String query = session.getUri().getQuery(); // e.g. roomId=1
        if (query != null && query.startsWith("roomId=")) {
            return Long.parseLong(query.split("=")[1]);
        }
        throw new BusinessException(ResponseCode.BAD_REQUEST);
    }
}
