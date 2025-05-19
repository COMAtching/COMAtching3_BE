package comatching.comatching3.users.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum HobbyEnum {
    스포츠("스포츠"),
    예술("예술"),
    문화("문화"),
    여행("여행"),
    자기계발("자기계발"),
    게임("게임"),
    UNSELECTED("UNSELECTED");

    private final String value;

    HobbyEnum(String value) {
        this.value = value;
    }

    @JsonCreator
    public static HobbyEnum from(String value) {
        for (HobbyEnum hobbyEnum : HobbyEnum.values()) {
            if (hobbyEnum.getValue().equals(value)) {
                return hobbyEnum;
            }
        }
        return null;
    }
}
