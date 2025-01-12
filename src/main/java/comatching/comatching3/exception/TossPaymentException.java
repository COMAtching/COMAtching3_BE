package comatching.comatching3.exception;

import lombok.Getter;

@Getter
public class TossPaymentException extends RuntimeException {

	private final TossPaymentExceptionDto tossPaymentExceptionDto;

	public TossPaymentException(TossPaymentExceptionDto tossPaymentExceptionDto) {
		super(tossPaymentExceptionDto.getMessage());
		this.tossPaymentExceptionDto = tossPaymentExceptionDto;
	}
}


