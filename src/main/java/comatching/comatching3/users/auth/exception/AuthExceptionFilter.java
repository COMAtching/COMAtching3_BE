package comatching.comatching3.users.auth.exception;

import comatching.comatching3.admin.exception.AccountIdAlreadyExistsException;
import comatching.comatching3.util.Response;
import comatching.comatching3.util.ResponseCode;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class AuthExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, e);
        } catch (AccountIdAlreadyExistsException e) {
            setErrorResponse(HttpStatus.CONFLICT, response, e);
        }
    }

    private void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");

        Response<?> res;
        if ("TOKEN_EXPIRE".equals(ex.getMessage())) {
            res = Response.errorResponse(ResponseCode.TOKEN_EXPIRED);
            log.info("[JwtExceptionFilter] - EXPIRE");
        } else if ("TOKEN_INVALID".equals(ex.getMessage())) {
            res = Response.errorResponse(ResponseCode.TOKEN_NOT_AVAILABLE);
            log.info("[JwtExceptionFilter] - INVALID");
        } else if ("ACCOUNT_ID_DUPLICATED".equals(ex.getMessage())) {
            res = Response.errorResponse(ResponseCode.ACCOUNT_ID_DUPLICATED);
            log.info("[JwtExceptionFilter] - ACCOUNT_ID_DUPLICATED");
        } else if ("INVALID_ADMIN_LOGIN".equals(ex.getMessage())) {
            res = Response.errorResponse(ResponseCode.INVALID_ADMIN_LOGIN);
            log.info("[JwtExceptionFilter] - INVALID_ADMIN_LOGIN");
        } else {
            res = Response.errorResponse(ResponseCode.GENERAL_ERROR);
            log.info("[JwtExceptionFilter] - GENERAL_ERROR");
        }

        String resJson = res.convertToJson();
        response.getWriter().write(resJson);
        log.info("[JwtExceptionFilter]: {}", resJson);
    }
}
