package comatching.comatching3.chat.domain.dto;

import comatching.comatching3.chat.domain.ChatRole;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@Data
public class ChatResponse {

    private LocalDateTime timestamp;
    private String content;
    private ChatRole role;


    public ChatResponse(LocalDateTime timestamp, String content, ChatRole role) {
        this.timestamp = timestamp;
        this.content = decodeContent(content);
        this.role = role;
    }

    public String decodeContent(String content) {
        return new String(
            Base64.getDecoder().decode(content),
            StandardCharsets.UTF_8
        );
    }
}