package comatching.comatching3.pay.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import comatching.comatching3.exception.TossPaymentExceptionDto;
import comatching.comatching3.pay.dto.res.TossPaymentRes;

@Service
public class PayErrorService {

	public TossPaymentExceptionDto makePaymentExceptionDto(HttpStatus status, TossPaymentRes response) {

		String code = response.getError().getCode();
		String message = response.getError().getMessage();


		return TossPaymentExceptionDto.builder()
			.status(status.value())
			.httpStatus(status)
			.code(code)
			.message(message)
			.build();
	}
}

