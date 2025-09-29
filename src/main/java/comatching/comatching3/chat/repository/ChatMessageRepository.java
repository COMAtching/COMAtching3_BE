package comatching.comatching3.chat.repository;

import comatching.comatching3.chat.domain.entity.ChatMessage;
import comatching.comatching3.chat.domain.entity.ChatRoom;
import comatching.comatching3.users.entity.Users;
import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderByCreatedAt(ChatRoom chatRoom);

    // 마지막 메시지 가져오기 (Spring Data JPA 메서드 네이밍으로 대체)
    Optional<ChatMessage> findTopByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);

    // 특정 시간 이후, 내가 보낸 것 제외한 메시지 개수
    long countByChatRoomAndCreatedAtAfterAndSenderNot(ChatRoom chatRoom, LocalDateTime createdAt, Users sender);

    // lastReadAt이 null인 경우 대비: 내가 보낸 것 제외 전체 메시지 개수
    long countByChatRoomAndSenderNot(ChatRoom chatRoom, Users sender);

    // 각 채팅방별 마지막 메시지
    @Query("SELECT m FROM ChatMessage m " +
        "WHERE m.chatRoom IN :chatRooms " +
        "AND m.createdAt = (" +
        "  SELECT MAX(m2.createdAt) FROM ChatMessage m2 WHERE m2.chatRoom = m.chatRoom" +
        ")")
    List<ChatMessage> findLastMessages(@Param("chatRooms") List<ChatRoom> chatRooms);

    // 안 읽은 메시지 개수 (lastReadAt 고려)
    @Query("SELECT m.chatRoom.id, COUNT(m) " +
        "FROM ChatMessage m " +
        "WHERE m.chatRoom IN :chatRooms " +
        "AND m.sender <> :me " +
        "AND (:lastReadAt IS NULL OR m.createdAt > :lastReadAt) " +
        "GROUP BY m.chatRoom.id")
    List<Object[]> countUnreadMessagesBatch(@Param("chatRooms") List<ChatRoom> chatRooms,
        @Param("lastReadAt") LocalDateTime lastReadAt,
        @Param("me") Users me);
}