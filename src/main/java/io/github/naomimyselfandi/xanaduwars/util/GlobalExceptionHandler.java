package io.github.naomimyselfandi.xanaduwars.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class GlobalExceptionHandler {

    record ErrorDto(String type, String details) {}

    @ExceptionHandler
    ResponseEntity<ErrorDto> handleResponseStatusException(Exception exception) throws Exception {
        var type = exception.getClass();
        var annotation = type.getAnnotation(ResponseStatus.class);
        if (annotation != null) {
            return new ResponseEntity<>(getBody(exception, type), getStatus(annotation));
        } else {
            throw exception; // Delegate to other handlers
        }
    }

    private static ErrorDto getBody(Exception exception, Class<? extends Exception> type) {
        return new ErrorDto(type.getSimpleName(), exception.getMessage());
    }

    private static HttpStatus getStatus(ResponseStatus annotation) {
        var value = annotation.value(); // Pick whichever one isn't the default
        return value == HttpStatus.INTERNAL_SERVER_ERROR ? annotation.code() : value;
    }

}
