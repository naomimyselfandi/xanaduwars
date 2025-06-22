package io.github.naomimyselfandi.xanaduwars.util;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/// An exception thrown to indicate an entity couldn't be found.
@StandardException
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchEntityException extends Exception {}
