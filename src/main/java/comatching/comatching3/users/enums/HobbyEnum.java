package comatching.comatching3.users.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum HobbyEnum {
    스포츠("스포츠"),
    예술("예술"),
    문화("문화"),
    여행("여행"),
    일상공부("일상/공부"),
    게임("게임"),
    UNSELECTED("UNSELECTED");

    private final String value;

    HobbyEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static HobbyEnum from(String value) {
        for (HobbyEnum hobbyEnum : HobbyEnum.values()) {
            if (hobbyEnum.getValue().equals(value)) {
                return hobbyEnum;
            }
        }
        return null;
    }
}
