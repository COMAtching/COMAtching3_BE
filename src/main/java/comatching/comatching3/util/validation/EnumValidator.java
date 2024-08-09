package comatching.comatching3.util.validation;

import java.util.Collection;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, Object> {

	private Class<? extends Enum<?>> enumClass;

	@Override
	public void initialize(ValidEnum constraintAnnotation) {
		this.enumClass = constraintAnnotation.enumClass();
	}

	/**
	 *
	 * @param value object to validate
	 * @param context context in which the constraint is evaluated
	 *
	 * @return if value is not enum or Collection<Enum> return false
	 */
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		if (value instanceof Enum) {
			return isValidEnum((Enum<?>) value);
		} else if (value instanceof Collection<?>) {
			return ((Collection<?>) value).stream()
				.allMatch(item -> item instanceof Enum && isValidEnum((Enum<?>) item));
		}

		return false;
	}

	private boolean isValidEnum(Enum<?> enumValue) {
		return enumValue.getClass().equals(enumClass);
	}

}
