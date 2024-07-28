package comatching.comatching3.util;

import java.util.ArrayList;
import java.util.List;

import comatching.comatching3.users.enums.Hobby;
import jakarta.persistence.AttributeConverter;

public class HobbyListConverter implements AttributeConverter<List<Hobby>, String> {
	@Override
	public String convertToDatabaseColumn(List<Hobby> attribute) {
		if (attribute == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		for (Hobby hobby : attribute) {
			if (result.length() > 0) {
				result.append(",");
			}
			result.append(hobby.getValue().toString());
		}
		return result.toString();
	}

	@Override
	public List<Hobby> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isEmpty()) {
			return new ArrayList<>();
		}
		List<Hobby> result = new ArrayList<>();
		for (String hobby : dbData.split(",")) {
			if (!hobby.isEmpty()) {
				result.add(Hobby.valueOf(hobby));
			}
		}
		return result;
	}
}
