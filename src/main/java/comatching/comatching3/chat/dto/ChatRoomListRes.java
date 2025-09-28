package comatching.comatching3.chat.dto;

import comatching.comatching3.chat.domain.dto.ChatRoomInfoRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomListRes {

	private ChatRoomInfoRes chatRoomInfoRes;
	private Long unreadCount;
	private String lastMessage;
}
