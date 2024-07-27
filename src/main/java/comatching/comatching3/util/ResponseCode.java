package comatching.comatching3.util;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ResponseCode {

	//General response
	SUCCESS(200, "GEN-000", HttpStatus.OK, "Success"),
	BAD_REQUEST(400, "GEN-001", HttpStatus.BAD_REQUEST, "Bad Request"),
	INTERNAL_SERVER_ERROR(500, "GEN-002", HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred"),

	//Auth response
	ACCOUNT_ID_DUPLICATED(409, "AUTH-001", HttpStatus.CONFLICT, "AccountId is duplicated"),
	INVALID_ADMIN_LOGIN(401, "AUTH-002", HttpStatus.UNAUTHORIZED, "Invalid AccountId or Password"),
	USER_NOT_FOUND(404, "AUTH-003", HttpStatus.NOT_FOUND, "Cannot found user or userId"),

	//Security response
	TOKEN_EXPIRED(401, "SE제C-001", HttpStatus.UNAUTHORIZED, "token is expired or not available"),
	TOKEN_NOT_AVAILABLE(401, "SEC-002", HttpStatus.UNAUTHORIZED, "token is not available"),

	ARGUMENT_NOT_VALID(400, "VAL-001", HttpStatus.BAD_REQUEST, "Argument not valid");

	private final Integer status;
	private final String code;
	private final HttpStatus httpStatus;
	private final String message;

	ResponseCode(Integer status, String code, HttpStatus httpStatus, String message) {
		this.status = status;
		this.code = code;
		this.httpStatus = httpStatus;
		this.message = message;
	}

}
