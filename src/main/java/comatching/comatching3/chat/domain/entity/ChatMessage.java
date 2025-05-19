package comatching.comatching3.chat.domain.entity;

import comatching.comatching3.chat.domain.ChatRole;
import comatching.comatching3.chat.domain.dto.ChatResponse;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.util.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ChatRoom chatRoom;

    @ManyToOne
    private Users sender;

    private String content;

    private ChatRole chatRole;

    public ChatMessage(Users sender, String content, ChatRoom chatRoom, ChatRole chatRole) {
        this.sender = sender;
        this.content = content;
        this.chatRoom = chatRoom;
        this.chatRole = chatRole;
    }

    public ChatResponse toResponse() {
        return new ChatResponse(this.getCreatedAt(), this.content, this.chatRole);
    }
}

