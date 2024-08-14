package comatching.comatching3.admin.exception;

import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AdminExceptionHandler {

    @ExceptionHandler(InvalidLoginException.class)
    public Response<?> handleInvalidLoginException(InvalidLoginException ex) {
        return Response.errorResponse(ResponseCode.INVALID_LOGIN);
    }

    @ExceptionHandler(AccountIdDuplicatedException.class)
    public Response<?> handleAccountIdDuplicatedException(AccountIdDuplicatedException ex) {
        return Response.errorResponse(ResponseCode.ACCOUNT_ID_DUPLICATED);
    }

}
