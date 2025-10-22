package io.github.naomimyselfandi.xanaduwars.auth.value;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/// An exception indicating invalid or missing credentials.
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

    /// A generic message that doesn't risk leaking sensitive information.
    public static final String INVALID_CREDENTIALS = "Invalid credentials.";

    /// Construct an unauthorized exception with a generic message.
    public UnauthorizedException() {
        super(INVALID_CREDENTIALS);
    }

}
