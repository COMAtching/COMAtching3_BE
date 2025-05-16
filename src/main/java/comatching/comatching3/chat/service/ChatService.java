package comatching.comatching3.chat.service;

import com.vane.badwordfiltering.BadWordFiltering;
import comatching.comatching3.chat.domain.ChatRole;
import comatching.comatching3.chat.domain.entity.ChatMessage;
import comatching.comatching3.chat.domain.entity.ChatRoom;
import comatching.comatching3.chat.repository.ChatMessageRepository;
import comatching.comatching3.chat.repository.ChatRoomRepository;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;


    public void createChatRoom(Users picker, Users picked) {

        ChatRoom newChatRoom = new ChatRoom(picker, picked);
        chatRoomRepository.save(newChatRoom);
    }

    public void saveChatMessage(Users sender, String content, Long ChatRoomNumber) {
        BadWordFiltering badWordFiltering = new BadWordFiltering();
        ChatRoom chatRoom = chatRoomRepository.findById(ChatRoomNumber)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        ChatMessage chatMessage;

        if (badWordFiltering.blankCheck(content)) throw new BusinessException(ResponseCode.BAD_REQUEST);

        if (sender.getId().equals(chatRoom.getPicker().getId())) {
            chatMessage = new ChatMessage(sender, content, chatRoom, ChatRole.PICKER);
        } else if (sender.getId().equals(chatRoom.getPicked().getId())) {
            chatMessage = new ChatMessage(sender, content, chatRoom, ChatRole.PICKED);
        } else {
            throw new BusinessException(ResponseCode.BAD_REQUEST);
        }

        chatMessageRepository.save(chatMessage);
    }
}
