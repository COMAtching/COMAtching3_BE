package comatching.comatching3.chat.dto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import comatching.comatching3.chat.domain.dto.ChatRoomInfoRes;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class ChatRoomListRes {

	private ChatRoomInfoRes chatRoomInfoRes;
	private Long unreadCount;
	private String lastMessage;

	public ChatRoomListRes(ChatRoomInfoRes chatRoomInfoRes, Long unreadCount, String lastMessage) {
		this.chatRoomInfoRes = chatRoomInfoRes;
		this.unreadCount = unreadCount;
		this.lastMessage = lastMessage;
	}
}
