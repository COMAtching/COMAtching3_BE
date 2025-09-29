package comatching.comatching3.chat.domain.dto;

import java.util.List;

import comatching.comatching3.users.dto.response.UserInfoRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDetailRes {

	private List<ChatResponse> chatMessages;
	private UserInfoRes opponentProfile;

}
