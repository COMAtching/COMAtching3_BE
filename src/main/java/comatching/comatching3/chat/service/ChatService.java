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
import comatching.comatching3.users.dto.response.UserInfoRes;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.users.service.UserService;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

        ChatRoomUser pickerMapping = ChatRoomUser.builder()
            .chatRoom(newChatRoom)
            .user(picker)
            .lastReadAt(LocalDateTime.now()) // 처음 생성 시 현재시간으로 초기화 가능
            .build();

        ChatRoomUser pickedMapping = ChatRoomUser.builder()
            .chatRoom(newChatRoom)
            .user(picked)
            .lastReadAt(LocalDateTime.now())
            .build();

        chatRoomUserRepository.save(pickerMapping);
        chatRoomUserRepository.save(pickedMapping);

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

        // 1. 채팅방 + picker/picked + userAiFeature fetch join으로 미리 가져오기
        List<ChatRoom> chatRooms = chatRoomRepository.findAllWithUsersAndFeaturesByUser(me);

        if (chatRooms.isEmpty()) {
            return List.of();
        }

        // 2. 마지막 메시지 batch 조회
        List<ChatMessage> lastMessages = chatMessageRepository.findLastMessages(chatRooms);
        Map<Long, ChatMessage> lastMessageMap = lastMessages.stream()
            .collect(Collectors.toMap(m -> m.getChatRoom().getId(), m -> m));

        // 3. 안 읽은 메시지 개수 batch 조회
        Map<Long, LocalDateTime> lastReadAtMap = chatRooms.stream()
            .collect(Collectors.toMap(ChatRoom::getId, cr -> getLastReadAt(cr, me)));

        Map<Long, Long> unreadCountMap = new HashMap<>();

        for (ChatRoom cr : chatRooms) {
            LocalDateTime lastReadAt = lastReadAtMap.get(cr.getId());
            List<Object[]> unreadResults = chatMessageRepository.countUnreadMessagesBatch(List.of(cr), lastReadAt, me);
            long count = unreadResults.isEmpty() ? 0L : (Long) unreadResults.get(0)[1];
            unreadCountMap.put(cr.getId(), count);
        }

        // 4. DTO 변환
        List<ChatRoomListRes> results = new ArrayList<>();
        for (ChatRoom chatRoom : chatRooms) {
            boolean isPicker = chatRoom.getPicker().getId().equals(me.getId());

            ChatMessage lastMsg = lastMessageMap.get(chatRoom.getId());

            if (!isPicker && lastMsg == null) {
                continue;
            }

            String lastMessage = (lastMsg != null)
                ? new String(Base64.getDecoder().decode(lastMsg.getContent()), StandardCharsets.UTF_8)
                : null;
            LocalDateTime lastMessageTimestamp = (lastMsg != null) ? lastMsg.getCreatedAt() : null;

            long unreadCount = unreadCountMap.getOrDefault(chatRoom.getId(), 0L);

            ChatRoomInfoRes info = new ChatRoomInfoRes(
                chatRoom.getId(),
                isPicker ? ChatRole.PICKER : ChatRole.PICKED,
                chatRoom.getPicker().getUsername(),
                chatRoom.getPicked().getUsername(),
                chatRoom.getPicker().getUserAiFeature().getMajor(),
                chatRoom.getPicker().getUserAiFeature().getAge(),
                chatRoom.getPicked().getUserAiFeature().getMajor(),
                chatRoom.getPicked().getUserAiFeature().getAge()
            );

            Users opponent = isPicker ? chatRoom.getPicked() : chatRoom.getPicker();
            UserInfoRes userInfoRes = UserInfoRes.from(opponent, opponent.getUserAiFeature().getHobbyNameList());

            results.add(new ChatRoomListRes(
                info,
                unreadCount,
                lastMessage,
                lastMessageTimestamp,
                userInfoRes
            ));
        }

        return results;

        // for (ChatRoom chatRoom : me.getAllChatRooms()) {
        //     boolean isPicker = chatRoom.getPicker().getId().equals(me.getId());
        //
        //     if (!isPicker && chatRoom.getChatMessages().isEmpty()) {
        //         continue;
        //     }
        //
        //     // 마지막 메시지 조회
        //     Optional<ChatMessage> lastMessageOpt = chatMessageRepository.findTopByChatRoomOrderByCreatedAtDesc(chatRoom);
        //
        //     String lastMessage = lastMessageOpt
        //         .map(m -> new String(Base64.getDecoder().decode(m.getContent()), StandardCharsets.UTF_8))
        //         .orElse(null);
        //
        //     LocalDateTime lastMessageTimestamp = lastMessageOpt
        //         .map(ChatMessage::getCreatedAt)
        //         .orElse(null);
        //
        //     // 마지막 읽은 시간
        //     LocalDateTime lastReadAt = getLastReadAt(chatRoom, me);
        //
        //     // 안 읽은 개수
        //     long unreadCount = (lastReadAt != null)
        //         ? chatMessageRepository.countByChatRoomAndCreatedAtAfterAndSenderNot(chatRoom, lastReadAt, me)
        //         : chatMessageRepository.countByChatRoomAndSenderNot(chatRoom, me);
        //
        //     ChatRoomInfoRes info = new ChatRoomInfoRes(
        //         chatRoom.getId(),
        //         chatRoom.getPicker().getId().equals(me.getId()) ? ChatRole.PICKER : ChatRole.PICKED,
        //         chatRoom.getPicker().getUsername(),
        //         chatRoom.getPicked().getUsername(),
        //         chatRoom.getPicker().getUserAiFeature().getMajor(),
        //         chatRoom.getPicker().getUserAiFeature().getAge(),
        //         chatRoom.getPicked().getUserAiFeature().getMajor(),
        //         chatRoom.getPicked().getUserAiFeature().getAge()
        //     );
        //
        //     Users enemy = chatRoom.getPicked();
        //     Users applier = chatRoom.getPicker();
        //
        //     UserInfoRes userInfoRes;
        //
        //     if (isPicker) {
        //         userInfoRes = UserInfoRes.from(enemy, enemy.getUserAiFeature().getHobbyNameList());
        //     } else {
        //         userInfoRes = UserInfoRes.from(applier, applier.getUserAiFeature().getHobbyNameList());
        //     }
        //
        //     ChatRoomListRes res = new ChatRoomListRes(
        //         info,
        //         unreadCount,
        //         lastMessage,
        //         lastMessageTimestamp,
        //         userInfoRes
        //     );
        //     results.add(res);
        // }

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

        ChatRoomUser cru = chatRoomUserRepository.findByChatRoomAndUser(chatRoom, user)
            .orElseThrow(() -> new BusinessException(ResponseCode.BAD_REQUEST));
        cru.setLastReadAt(LocalDateTime.now());
        chatRoomUserRepository.save(cru);

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
