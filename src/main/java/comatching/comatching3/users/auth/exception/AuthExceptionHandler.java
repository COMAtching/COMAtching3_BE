package comatching.comatching3.users.auth.exception;

import comatching.comatching3.admin.exception.AccountIdDuplicatedException;
import comatching.comatching3.admin.exception.InvalidLoginException;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<Response<?>> handleInvalidLoginException(InvalidLoginException ex) {
        Response<?> response = Response.errorResponse(ResponseCode.INVALID_ADMIN_LOGIN);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccountIdDuplicatedException.class)
    public ResponseEntity<Response<?>> handleAccountIdDuplicatedException(AccountIdDuplicatedException ex) {
        Response<?> response = Response.errorResponse(ResponseCode.ACCOUNT_ID_DUPLICATED);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

}
