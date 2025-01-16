package comatching.comatching3.util.Idempotent.Exception;

import comatching.comatching3.util.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class IdempotentException extends RuntimeException{

    private final ResponseCode responseCode;
}
