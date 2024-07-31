package comatching.comatching3.exception;

import comatching.comatching3.util.ResponseCode;
import lombok.Builder;
import lombok.Getter;

public class BusinessException extends RuntimeException{
	@Getter
	private final ResponseCode responseCode;

	@Builder
	public BusinessException(ResponseCode responseCode) {
		super(responseCode.getMessage());
		this.responseCode= responseCode;
	}

}
