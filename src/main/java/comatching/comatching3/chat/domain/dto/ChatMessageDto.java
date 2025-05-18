package comatching.comatching3.chat.domain.dto;


import comatching.comatching3.chat.domain.ChatRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDto {
    private Long chatRoomId;
    private String content;
    private ChatRole chatRole;
}
