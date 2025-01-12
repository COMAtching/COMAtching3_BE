package comatching.comatching3.exception;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TossPaymentExceptionDto {
	private Integer status;
	private String code;
	private HttpStatus httpStatus;
	private String message;

	@Builder
	public TossPaymentExceptionDto(Integer status, String code, HttpStatus httpStatus, String message) {
		this.status = status;
		this.code = code;
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
