package io.github.naomimyselfandi.xanaduwars.util;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/// An exception indicating that a request conflicts with the current state.
@StandardException
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {}
