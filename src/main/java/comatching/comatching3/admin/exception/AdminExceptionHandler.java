package comatching.comatching3.admin.exception;

import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AdminExceptionHandler {

    @ExceptionHandler(AccountIdDuplicatedException.class)
    public Response<?> handleAccountIdDuplicatedException(AccountIdDuplicatedException ex) {
        return Response.errorResponse(ResponseCode.ACCOUNT_ID_DUPLICATED);
    }

    @ExceptionHandler(UniversityNotExistException.class)
    public Response<?> handleUniversityNotExistException(UniversityNotExistException ex) {
        return Response.errorResponse(ResponseCode.ARGUMENT_NOT_VALID);
    }

}
