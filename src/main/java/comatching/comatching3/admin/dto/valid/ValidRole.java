package comatching.comatching3.admin.dto.valid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = RoleValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRole {
	String message() default "유효하지 않은 역할입니다."; // 기본 메시지
	Class<?>[] groups() default {}; // 그룹 지정
	Class<? extends Payload>[] payload() default {}; // 메타데이터
}
