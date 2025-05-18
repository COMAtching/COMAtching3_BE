package comatching.comatching3.chat.domain.dto;

import comatching.comatching3.chat.domain.ChatRole;

import java.time.LocalDateTime;

public class ChatResponse {

    private LocalDateTime timestamp;
    private String content;
    private ChatRole role;


    public ChatResponse(LocalDateTime timestamp, String content, ChatRole role) {
        this.timestamp = timestamp;
        this.content = content;
        this.role = role;
    }
}