package comatching.comatching3.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import comatching.comatching3.util.Idempotent.Exception.IdempotentException;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
	public Response<?> handleValidationExceptions(Exception ex) {
		Map<String, String> errors = new HashMap<>();

		if (ex instanceof MethodArgumentNotValidException) {
			MethodArgumentNotValidException manve = (MethodArgumentNotValidException)ex;
			manve.getBindingResult().getAllErrors().forEach((error) -> {
				String fieldName = ((FieldError)error).getField();
				String errorMessage = error.getDefaultMessage();
				errors.put(fieldName, errorMessage);
			});
		} else if (ex instanceof ConstraintViolationException) {
			ConstraintViolationException cve = (ConstraintViolationException)ex;
			cve.getConstraintViolations().forEach(violation -> {
				String fieldName = violation.getPropertyPath().toString();
				String errorMessage = violation.getMessage();
				errors.put(fieldName, errorMessage);
			});
		}
		return Response.errorResponse(ResponseCode.ARGUMENT_NOT_VALID);
	}

	@ExceptionHandler(BusinessException.class)
	public Response<ResponseCode> handleCustomException(BusinessException ex) {
		return new Response<>(ex.getResponseCode());
	}

	@ExceptionHandler(TossPaymentException.class)
	public Response<TossPaymentExceptionDto> handleTossPaymentException(TossPaymentException ex) {
		return new Response<>(ex.getTossPaymentExceptionDto());
	}

	@ExceptionHandler(RequestNotPermitted.class)
	public Response<?> handleRateLimitException(RequestNotPermitted ex) {
		return Response.errorResponse(ResponseCode.OVER_REQUEST_LIMIT);
	}

	@ExceptionHandler({IdempotentException.class})
	public ResponseEntity handelIdempotentException(IdempotentException ex) {
		return ResponseEntity
			.status(ex.getResponseCode().getHttpStatus())
			.body(new Response<>(ex.getResponseCode()));
	}
}
