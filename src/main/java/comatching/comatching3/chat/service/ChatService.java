package comatching.comatching3.chat.service;

import com.vane.badwordfiltering.BadWordFiltering;
import comatching.comatching3.chat.domain.ChatRole;
import comatching.comatching3.chat.domain.dto.ChatResponse;
import comatching.comatching3.chat.domain.dto.ChatRoomInfoRes;
import comatching.comatching3.chat.domain.entity.ChatMessage;
import comatching.comatching3.chat.domain.entity.ChatRoom;
import comatching.comatching3.chat.domain.entity.ChatRoomUser;
import comatching.comatching3.chat.dto.ChatRoomListRes;
import comatching.comatching3.chat.repository.ChatMessageRepository;
import comatching.comatching3.chat.repository.ChatRoomRepository;
import comatching.comatching3.chat.repository.ChatRoomUserRepository;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final UsersRepository usersRepository;
    private final SecurityUtil securityUtil;

    /**
     * 채팅방 생성 메서드
     *
     * @param picker
     * @param picked
     */
    public Long createChatRoom(Users picker, Users picked) {

        ChatRoom newChatRoom = new ChatRoom(picker, picked);
        chatRoomRepository.save(newChatRoom);

        return newChatRoom.getId();
    }

    /**
     * 대화 저장 메서드
     *
     * @param sender
     * @param content
     * @param ChatRoomNumber
     * @return
     */
    public Response saveChatMessage(Users sender, String content, Long ChatRoomNumber) {

        Response response;
        BadWordFiltering badWordFiltering = new BadWordFiltering();
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(ChatRoomNumber);
        if (optionalChatRoom.isEmpty()) {
            return Response.errorResponse(ResponseCode.MATCH_CODE_CHECK_FAIL);
        }

        ChatRoom chatRoom = optionalChatRoom.get();
        ChatMessage chatMessage;

        if (badWordFiltering.blankCheck(content)) return Response.errorResponse(ResponseCode.BAD_WORD_INCLUDE);

        if (sender.getId().equals(chatRoom.getPicker().getId())) {
            chatMessage = new ChatMessage(sender, content, chatRoom, ChatRole.PICKER);
        } else if (sender.getId().equals(chatRoom.getPicked().getId())) {
            chatMessage = new ChatMessage(sender, content, chatRoom, ChatRole.PICKED);
        } else {
            return Response.errorResponse(ResponseCode.BAD_REQUEST);
        }

        chatMessageRepository.save(chatMessage);

        ChatResponse chatResponse = new ChatResponse(chatMessage.getCreatedAt(), chatMessage.getContent(), chatMessage.getChatRole());
        return Response.ok(chatResponse);
    }

    public void createChatRoom(Long pickerId, Long pickedId) {
        Users picker = usersRepository.findById(pickerId).orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
        Users picked = usersRepository.findById(pickedId).orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        ChatRoom newChatRoom = new ChatRoom(picker, picked);
        chatRoomRepository.save(newChatRoom);
    }


    /**
     * 유저가 속한 채팅방 내역 조회
     *
     * @return
     */
    public List<ChatRoomListRes> getChatRoomList() {
        Users me = securityUtil.getCurrentUsersEntity();
        List<ChatRoomListRes> results = new ArrayList<>();

        for (ChatRoom chatRoom : me.getAllChatRooms()) {
            // 마지막 메시지
            String lastMessage = chatMessageRepository.findTopByChatRoomOrderByCreatedAtDesc(chatRoom)
                .map(ChatMessage::getContent)
                .orElse(null);

            // 마지막 읽은 시간
            LocalDateTime lastReadAt = getLastReadAt(chatRoom, me);

            // 안 읽은 개수
            long unreadCount = (lastReadAt != null)
                ? chatMessageRepository.countByChatRoomAndCreatedAtAfterAndSenderNot(chatRoom, lastReadAt, me)
                : chatMessageRepository.countByChatRoomAndSenderNot(chatRoom, me);

            ChatRoomInfoRes info = new ChatRoomInfoRes(
                chatRoom.getId(),
                chatRoom.getPicker().getId().equals(me.getId()) ? ChatRole.PICKER : ChatRole.PICKED,
                chatRoom.getPicker().getUsername(),
                chatRoom.getPicked().getUsername(),
                chatRoom.getPicker().getUserAiFeature().getMajor(),
                chatRoom.getPicker().getUserAiFeature().getAge(),
                chatRoom.getPicked().getUserAiFeature().getMajor(),
                chatRoom.getPicked().getUserAiFeature().getAge()
            );

            ChatRoomListRes res = new ChatRoomListRes(info, unreadCount, lastMessage);
            results.add(res);
        }

        return results;
    }

    /**
     * 채팅방 대화 내역 조회
     *
     * @param roomId
     * @return
     */
    public List<ChatResponse> getRoomChats(Long roomId) {

        List<ChatResponse> response = new ArrayList<>();
        Users user = securityUtil.getCurrentUsersEntity();

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ResponseCode.BAD_REQUEST));

        Long pickerId = chatRoom.getPicker().getId();
        Long pickedId = chatRoom.getPicked().getId();

        if (!pickerId.equals(user.getId()) && !pickedId.equals(user.getId())) {
            throw new BusinessException(ResponseCode.BAD_REQUEST);
        }

        List<ChatMessage> chats = chatMessageRepository.findByChatRoomOrderByCreatedAt(chatRoom);
        for (ChatMessage chat : chats) {
            response.add(chat.toResponse());
        }

        return response;

    }

    private LocalDateTime getLastReadAt(ChatRoom chatRoom, Users me) {
        return chatRoomUserRepository.findByChatRoomAndUser(chatRoom, me)
            .map(ChatRoomUser::getLastReadAt)
            .orElse(null);
    }
}
