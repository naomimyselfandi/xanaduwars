package io.github.naomimyselfandi.xanaduwars.auth.jwt;

import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;

import java.time.Duration;

/// A service that calculates the duration of a JWT.
public interface JWTDurationService {

    /// Whether "remember me" mode is in effect, if applicable.
    enum RememberMe {YES, NO}

    /// Calculate the duration a JWT should be valid for.
    Duration duration(UserDetailsDto dto, JWTPurpose purpose, RememberMe rememberMe);

}
