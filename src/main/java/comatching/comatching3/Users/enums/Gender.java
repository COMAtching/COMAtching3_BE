package comatching.comatching3.Users.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public enum Gender {
	MALE(0, "남자"),
	FEMALE(1, "여자");

	private final Integer vector;
	private final String value;

	Gender(Integer vector, String value) {
		this.vector = vector;
		this.value = value;
	}

	@JsonCreator
	public static Gender from(String value) {
		for (Gender gender : Gender.values()) {
			if (gender.getValue().equals(value)) {
				return gender;
			}
		}
		return null;
	}

	@JsonValue
	public String getValue() {
		return value;
	}
}
