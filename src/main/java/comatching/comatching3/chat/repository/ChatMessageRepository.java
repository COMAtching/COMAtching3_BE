package comatching.comatching3.chat.repository;

import comatching.comatching3.chat.domain.entity.ChatMessage;
import comatching.comatching3.chat.domain.entity.ChatRoom;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderByCreatedAt(ChatRoom chatRoom);
}
