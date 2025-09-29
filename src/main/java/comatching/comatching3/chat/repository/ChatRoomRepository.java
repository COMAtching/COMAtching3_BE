package comatching.comatching3.chat.repository;

import comatching.comatching3.chat.domain.entity.ChatRoom;
import comatching.comatching3.users.entity.Users;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends CrudRepository<ChatRoom, Long> {


    Optional<ChatRoom> findByPickerAndPicked(Users picker, Users picked);

    Optional<ChatRoom> findByPickerAndPickedOrPickedAndPicker(Users a, Users b, Users b2, Users a2);

    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
        "JOIN FETCH cr.picker p " +
        "JOIN FETCH p.userAiFeature " +
        "JOIN FETCH cr.picked pk " +
        "JOIN FETCH pk.userAiFeature " +
        "JOIN cr.chatRoomUsers cru " +
        "WHERE cru.user = :user")
    List<ChatRoom> findAllWithUsersAndFeaturesByUser(@Param("user") Users user);
}
