package comatching.comatching3.util;

import java.util.ArrayList;
import java.util.List;

import comatching.comatching3.users.enums.HobbyEnum;
import jakarta.persistence.AttributeConverter;

public class HobbyListConverter implements AttributeConverter<List<HobbyEnum>, String> {
	@Override
	public String convertToDatabaseColumn(List<HobbyEnum> attribute) {
		if (attribute == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		for (HobbyEnum hobbyEnum : attribute) {
			if (result.length() > 0) {
				result.append(",");
			}
			result.append(hobbyEnum.getValue().toString());
		}
		return result.toString();
	}

	@Override
	public List<HobbyEnum> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isEmpty()) {
			return new ArrayList<>();
		}
		List<HobbyEnum> result = new ArrayList<>();
		for (String hobby : dbData.split(",")) {
			if (!hobby.isEmpty()) {
				result.add(HobbyEnum.valueOf(hobby));
			}
		}
		return result;
	}
}
