package io.github.naomimyselfandi.xanaduwars.util;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import lombok.experimental.StandardException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GlobalExceptionHandlerTest {

    @StandardException
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    private static class NotImplementedException extends Exception {}

    @StandardException
    @ResponseStatus(code = HttpStatus.I_AM_A_TEAPOT)
    private static class TeapotException extends Exception {}

    @ResponseStatus
    @StandardException
    private static class ServerErrorException extends Exception {}

    private GlobalExceptionHandler fixture;

    @BeforeEach
    void setup() {
        fixture = new GlobalExceptionHandler();
    }

    @MethodSource
    @ParameterizedTest
    void handleResponseStatusException(Exception exception, HttpStatus status) throws Exception {
        assertThat(fixture.handleResponseStatusException(exception)).isEqualTo(new ResponseEntity<>(
                new GlobalExceptionHandler.ErrorDto(exception.getClass().getSimpleName(), exception.getMessage()),
                status
        ));
    }

    @Test
    void handleResponseStatusException_WhenTheExceptionIsNotAnnotated_ThenThrows() {
        var e = new Exception();
        assertThatThrownBy(() -> fixture.handleResponseStatusException(e)).isSameAs(e);
    }

    private static Stream<Arguments> handleResponseStatusException() {
        return Stream.of(
                arguments(new NotImplementedException("try again l8r g8r"), HttpStatus.NOT_IMPLEMENTED),
                arguments(new TeapotException("here is my handle here is my spout"), HttpStatus.I_AM_A_TEAPOT),
                arguments(new ServerErrorException("server is in spin cycle"), HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }

}
