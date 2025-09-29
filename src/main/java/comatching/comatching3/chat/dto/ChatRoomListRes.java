package comatching.comatching3.chat.dto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

import comatching.comatching3.chat.domain.dto.ChatRoomInfoRes;
import comatching.comatching3.users.dto.response.UserInfoRes;
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
	private LocalDateTime lastMessageTimestamp;
	private UserInfoRes userInfoRes;
}
