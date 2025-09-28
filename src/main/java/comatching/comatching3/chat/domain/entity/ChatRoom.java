package comatching.comatching3.chat.domain.entity;

import java.util.ArrayList;
import java.util.List;

import comatching.comatching3.users.entity.Users;
import comatching.comatching3.util.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "picker_id")
    private Users picker;

    @ManyToOne
    @JoinColumn(name = "picked_id")
    private Users picked;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

    public ChatRoom(Users picker, Users picked) {
        this.picked = picked;
        this.picker = picker;
    }
    // Todo: equals/hashCode에서 picker/picked 조합으로 중복 방지
}