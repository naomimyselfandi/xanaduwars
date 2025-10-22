package io.github.naomimyselfandi.xanaduwars.core.model;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/// An exception thrown to indicate an invalid command.
@StandardException
@ResponseStatus(HttpStatus.CONFLICT)
public class CommandException extends Exception {}
