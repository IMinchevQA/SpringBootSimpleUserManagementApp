package com.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by Ivan Minchev on 11/13/2017.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleUsernameNotFoundException(Exception unfex) {
        return new ApiErrorResponse(
                String.format("%s %s!",
                    HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND.toString()),
                unfex.getMessage());
    }

    @ExceptionHandler(value = UnsupportedOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleUnsupportedOperationException(Exception usoex) {
        return new ApiErrorResponse(
                String.format("%s %s!",
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        HttpStatus.BAD_REQUEST.toString()),
                usoex.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleUnknownException(Exception ex) {
        return new ApiErrorResponse(
                String.format("%s %s!",
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        HttpStatus.INTERNAL_SERVER_ERROR.toString()),
                ex.getMessage());
    }
}
