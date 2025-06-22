package io.github.naomimyselfandi.xanaduwars.util;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/// An exception thrown to indicate an invalid operation.
@StandardException
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidOperationException extends Exception {}
