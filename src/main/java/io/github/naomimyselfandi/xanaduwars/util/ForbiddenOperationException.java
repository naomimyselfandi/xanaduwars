package io.github.naomimyselfandi.xanaduwars.util;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/// An exception thrown to indicate a forbidden operation.
@StandardException
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenOperationException extends Exception {}
