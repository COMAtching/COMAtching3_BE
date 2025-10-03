package comatching.comatching3.util;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {

    //General response
    SUCCESS(200, "GEN-000", HttpStatus.OK, "Success"),
    BAD_REQUEST(400, "GEN-001", HttpStatus.BAD_REQUEST, "Bad Request"),
    INTERNAL_SERVER_ERROR(500, "GEN-002", HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred"),

    //Auth response
    ACCOUNT_ID_DUPLICATED(409, "AUTH-001", HttpStatus.CONFLICT, "AccountId is duplicated"),
    INVALID_LOGIN(403, "AUTH-002", HttpStatus.FORBIDDEN, "Invalid AccountId or Password"),
    USER_NOT_FOUND(404, "AUTH-003", HttpStatus.NOT_FOUND, "Cannot found user"),
    PENDING_OPERATOR(400, "AUTH-004", HttpStatus.FORBIDDEN, "승인되지 않은 관리자"),
    ALREADY_CHANGED(400, "AUTH-005", HttpStatus.BAD_REQUEST, "관리자의 아이디는 한 번만 바꿀 수 있습니다."),
    NO_PERMISSION(400, "AUTH-006", HttpStatus.FORBIDDEN, "권한이 없습니다."),
    OVER_REQUEST_LIMIT(400, "AUTH-007", HttpStatus.BAD_REQUEST, "요청할 수 있는 제한을 초과했습니다."),


    //Security response
    TOKEN_EXPIRED(401, "SEC-001", HttpStatus.UNAUTHORIZED, "token is expired or not available"),
    TOKEN_NOT_AVAILABLE(401, "SEC-002", HttpStatus.UNAUTHORIZED, "token is not available"),
    NOT_SUPPORTED_PROVIDER(401, "SEC-003", HttpStatus.UNAUTHORIZED, "not supported provider"),
    JWT_ERROR(401, "SEC-004", HttpStatus.UNAUTHORIZED, "jwt error"),
    ALREADY_LOGOUT(401, "SEC-005", HttpStatus.UNAUTHORIZED, "user already logout"),
    BLACK_USER(403, "SEC-006", HttpStatus.FORBIDDEN, "블랙리스트에 있는 유저입니다."),

    //User Exception
    USER_REGISTER_FAIL(400, "USR-001", HttpStatus.BAD_REQUEST, "User register is fail"),
    NOT_ENOUGH_POINT(400, "USR-002", HttpStatus.BAD_REQUEST, "Not enough point"),
    INPUT_FEATURE_FAIL(400, "USR-003", HttpStatus.BAD_REQUEST, "User feature input fail"),
    ADD_PICKME_FAIL(400, "USR-004", HttpStatus.BAD_REQUEST, "Add pickMe failed"),
    ALREADY_PARTICIPATED(400, "USR-005", HttpStatus.BAD_REQUEST, "already participated event"),
    BAD_REQUEST_PICKME(400, "USR-006", HttpStatus.BAD_REQUEST, "Request object or Amount cannot be null"),
    INVALID_USERNAME(400, "USR-007", HttpStatus.BAD_REQUEST, "사용할 수 없는 닉네임"),

    //Validation exception response
    ARGUMENT_NOT_VALID(400, "VAL-001", HttpStatus.BAD_REQUEST, "Argument not valid"),
    SCHOOL_NOT_EXIST(400, "VAL-002", HttpStatus.BAD_REQUEST, "School not exist"),

    //Match service exception response
    MATCH_GENERAL_FAIL(400, "MAT-001", HttpStatus.BAD_REQUEST, "Matching request process is failed"),
    MATCH_TIME_OVER(400, "MAT-002", HttpStatus.BAD_REQUEST, "Match process time is over please try again"),
    MATCH_CODE_GENERATE_FAIL(400, "MAT-003", HttpStatus.BAD_REQUEST, "Match auth code generate failed"),
    MATCH_CODE_CHECK_FAIL(400, "MAT-004", HttpStatus.BAD_REQUEST, "Match auth code authentication failed"),
    NO_MATCH_RESPONSE(400, "MAT-005", HttpStatus.BAD_REQUEST, "Couldn't receive the response message from ai server"),
    NO_ENEMY_AVAILABLE(400, "MAT-006", HttpStatus.BAD_REQUEST, "매칭 대상이 없습니다. 포인트는 차감되지 않습니다."),
    INSUFFICIENT_POINT(400, "MAT-007", HttpStatus.BAD_REQUEST, "insufficient point to match"),
    MATCH_COUNT_OVER(400, "MAT-008", HttpStatus.BAD_REQUEST, "MATCH_COUNT_OVER"),


    //History exception response
    MATCH_HISTORY_NOT_EXIST(200, "MAH-003", HttpStatus.BAD_REQUEST, "Matching history is not exist"),

    //Payment exception response
    PAYMENT_FAIL(400, "PAY-001", HttpStatus.BAD_REQUEST, "Payment failed"),
    ENOUGH_DAILY_CHARGE(400, "PAY-002", HttpStatus.BAD_REQUEST, "일일 결제한도 30000원 초과"),
    ALREADY_REQUEST_CHARGE(400, "PAY-003", HttpStatus.BAD_REQUEST, "이미 대기중인 결제 요청이 있습니다"),

    //Idempotent exception response
    UNPROCESSABLE_ENTITY(422, "IDP-001", HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity"),
    CONFLICT(409, "IDP-002", HttpStatus.CONFLICT, "Conflict"),

    //Event
    EVENT_PERIOD_DUPLICATE(400, "EVT-001", HttpStatus.BAD_REQUEST, "Event period is duplicated!"),
    NO_EVENT(200, "EVT-002", HttpStatus.OK, "No event exist"),
    CANT_PARTICIPATE(200, "EVT-003", HttpStatus.OK, "You can't participate event"),
    EVENT_TIME_OVER(200, "EVT-004", HttpStatus.OK, "Event time over"),
    WRONG_EVENT_STATUS(400, "EVT-005", HttpStatus.BAD_REQUEST, "WRONG EVENT STATUS"),

    // Charge
    OVER_1000(400, "CHR-001", HttpStatus.BAD_REQUEST, "천원버튼 사용불가능한 포인트"),
    ALREADY_USE(400, "CHR-002", HttpStatus.BAD_REQUEST, "이미 천원 버튼 사용"),
    BUTTON_NOT_ACTIVE(400, "CHR-003", HttpStatus.BAD_REQUEST, "천원 버튼 비활성화 상태"),

    // Chat
    BAD_WORD_INCLUDE(200, "CHT-001", HttpStatus.OK, "비속어가 포함되어 있습니다"),
    NO_CHAT_ROOMS(200, "CHT-002", HttpStatus.OK, "채팅방이 없습니다"),
    NOT_USER_ROOM(200, "CHT-003", HttpStatus.OK, "소속된 채팅방이 아닙니다");

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
