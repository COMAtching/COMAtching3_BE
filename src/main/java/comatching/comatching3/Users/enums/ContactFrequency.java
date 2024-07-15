package comatching.comatching3.Users.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public enum ContactFrequency {

	FREQUENT(2, "자주"),
	NORMAL(1, "보통"),
	NOT_FREQUENT(0, "가끔");

	private final Integer vector;
	private final String value;

	ContactFrequency(Integer vector, String value) {
		this.vector = vector;
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

	@JsonValue
	public String getValue() {
		return value;
	}
}
