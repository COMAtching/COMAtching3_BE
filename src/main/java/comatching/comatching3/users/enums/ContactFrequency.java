package comatching.comatching3.users.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public enum ContactFrequency {

	FREQUENT("FREQUENT", "자주"),
	NORMAL("NORMAL", "보통"),
	NOT_FREQUENT("NOT_FREQUENT", "가끔");

	private final String aiValue;
	private final String value;

	ContactFrequency(String vector, String value) {
		this.aiValue = vector;
		this.value = value;
	}

	@JsonCreator
	public static ContactFrequency from(String value) {
		for (ContactFrequency status : ContactFrequency.values()) {
			if (status.getValue().equals(value)) {
				return status;
			}
		}
		return null;
	}

	public static ContactFrequency fromAiValue(String aiValue) {
		for (ContactFrequency status : ContactFrequency.values()) {
			if (status.getAiValue().equals(aiValue)) {
				return status;
			}
		}
		return null; // aiValue가 일치하지 않을 경우 null 반환
	}

	public static ContactFrequency valueToAiValue(String value) {
		for (ContactFrequency status : ContactFrequency.values()) {
			if (status.getValue().equals(value)) {
				return status;
			}
		}
		return null; // aiValue가 일치하지 않을 경우 null 반환
	}

	@JsonValue
	public String getValue() {
		return value;
	}
}
