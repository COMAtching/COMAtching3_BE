package comatching.comatching3.util;

import java.util.ArrayList;
import java.util.List;

import comatching.comatching3.users.enums.Hobby;
import jakarta.persistence.AttributeConverter;

public class HobbyListConverter implements AttributeConverter<List<Hobby>, String> {
	@Override
	public String convertToDatabaseColumn(List<Hobby> attribute) {
		StringBuilder result = new StringBuilder();
		for (Hobby hobby : attribute) {
			//System.out.println("[hobbyListConverter] - add >> " + hobby.getValue());
			result.append(hobby.getValue().toString() + ",");
		}

		//System.out.println("[hobbyListConverter] - result= " + result);
		return result.toString();
	}

	@Override
	public List<Hobby> convertToEntityAttribute(String dbData) {
		List<Hobby> result = new ArrayList<>();
		for (String hobby : dbData.split(",")) {
			if (!hobby.equals("")) {
				result.add(Hobby.valueOf(hobby));
			}
		}
		return result;
	}
}
