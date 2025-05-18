package comatching.comatching3.chat.repository;

import comatching.comatching3.chat.domain.entity.ChatRoom;
import comatching.comatching3.users.entity.Users;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ChatRoomRepository extends CrudRepository<ChatRoom, Long> {


    Optional<ChatRoom> findByPickerAndPicked(Users picker, Users picked);

    Optional<ChatRoom> findByPickerAndPickedOrPickedAndPicker(Users a, Users b, Users b2, Users a2);
}
