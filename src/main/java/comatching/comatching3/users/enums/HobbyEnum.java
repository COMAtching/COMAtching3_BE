package comatching.comatching3.users.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum HobbyEnum {
    운동(1, "운동"),
    스포츠시청(2, "스포츠시청"),
    요리(3, "요리"),
    맛집탐방(4, "맛집탐방"),
    예술관람(5, "예술관람"),
    음악감상(6, "음악감상"),
    악기(7, "악기"),
    사진(8, "사진"),
    테크(9, "테크"),
    창작(10, "창작"),
    야외활동(11, "야외활동"),
    여행(12, "여행"),
    ott시청(13, "ott시청"),
    게임(14, "게임"),
    독서(15, "독서"),
    UNSELECTED(-1, "UNSELECTED");

    private final Integer vector;
    private final String value;

    HobbyEnum(Integer vector, String value) {
        this.vector = vector;
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
