package comatching.comatching3.users.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public enum Gender {
	MALE("MALE", "남자"),
	FEMALE("FEMALE", "여자");

	private final String aiValue;
	private final String value;

	Gender(String vector, String value) {
		this.aiValue = vector;
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

	public static Gender fromAiValue(String aiValue) {
		for (Gender gender : Gender.values()) {
			if (gender.getAiValue().equals(aiValue)) {
				return gender;
			}
		}
		return null; // aiValue가 일치하지 않을 경우 null 반환
	}

	@JsonValue
	public String getValue() {
		return value;
	}
}
