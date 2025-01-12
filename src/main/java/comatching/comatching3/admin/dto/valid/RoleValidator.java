package comatching.comatching3.admin.dto.valid;

import comatching.comatching3.admin.enums.AdminRole;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RoleValidator implements ConstraintValidator<ValidRole, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isEmpty()) {
			return false;
		}

		// 조건 검사
		return value.equals(AdminRole.ROLE_SEMI_ADMIN.getRoleName()) ||
			value.equals(AdminRole.ROLE_SEMI_OPERATOR.getRoleName());
	}
}
