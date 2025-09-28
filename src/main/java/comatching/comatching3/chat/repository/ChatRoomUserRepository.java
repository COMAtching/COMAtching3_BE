package comatching.comatching3.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import comatching.comatching3.chat.domain.entity.ChatRoom;
import comatching.comatching3.chat.domain.entity.ChatRoomUser;
import comatching.comatching3.users.entity.Users;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {

	Optional<ChatRoomUser> findByChatRoomAndUser(ChatRoom chatRoom, Users user);
}
