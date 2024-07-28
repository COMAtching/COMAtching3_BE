package comatching.comatching3.users.exception;

import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public Response<?> handleUserNotFoundException(UserNotFoundException e) {
        return Response.errorResponse(ResponseCode.USER_NOT_FOUND);
    }
}
