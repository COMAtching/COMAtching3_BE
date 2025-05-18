package comatching.comatching3.chat.domain.dto;


import comatching.comatching3.chat.domain.ChatRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageDto {
    private Long chatRoomId;
    private String content;
    private ChatRole chatRole;

    public ChatResponse toChatResponse(LocalDateTime createdAt) {
        return new ChatResponse(createdAt, this.content, this.chatRole);
    }
}
