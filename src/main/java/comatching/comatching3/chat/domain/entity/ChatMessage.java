package comatching.comatching3.chat.domain.entity;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

    @Column(columnDefinition = "TEXT")
    private String content;

    private ChatRole chatRole;

    public ChatMessage(Users sender, String content, ChatRoom chatRoom, ChatRole chatRole) {
        this.sender = sender;
        this.content = encode(content);
        this.chatRoom = chatRoom;
        this.chatRole = chatRole;
    }

    public ChatResponse toResponse() {
        return new ChatResponse(this.getCreatedAt(), this.content, this.chatRole);
    }

    private String encode(String plainText) {
        return Base64.getEncoder()
            .encodeToString(plainText.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String encodedText) {
        return new String(Base64.getDecoder().decode(encodedText), StandardCharsets.UTF_8);
    }

}

