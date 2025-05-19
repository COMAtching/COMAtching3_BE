package comatching.comatching3.chat.domain.dto;


import comatching.comatching3.chat.domain.ChatRole;

import java.util.List;

public record ChatRoomInfoRes(Long roomId, ChatRole myRole, List<ChatMessageDto> messages, String pickerName,
                              String pickedName) {
}
