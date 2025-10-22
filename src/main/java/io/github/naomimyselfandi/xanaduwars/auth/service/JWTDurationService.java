package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;

import java.time.Duration;

/// A service that calculates the duration of a JWT.
interface JWTDurationService {

    /// Calculate the duration a JWT should be valid for.
    Duration getDuration(UserDetailsDto dto, JWTPurpose purpose);

}
