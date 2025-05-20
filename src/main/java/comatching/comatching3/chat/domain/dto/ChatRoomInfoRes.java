package comatching.comatching3.chat.domain.dto;


import comatching.comatching3.chat.domain.ChatRole;

public record ChatRoomInfoRes(Long roomId, ChatRole myRole, String pickerName,
                              String pickedName, String pickerMajor, Integer pickerAge, String pickedMajor,
                              Integer pickedAge) {
}
