package io.github.naomimyselfandi.xanaduwars.core.service;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/// An exception thrown to indicate an invalid spell choice.
@StandardException
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class SpellChoiceException extends Exception {}
