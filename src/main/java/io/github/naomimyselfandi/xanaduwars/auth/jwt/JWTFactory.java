package io.github.naomimyselfandi.xanaduwars.auth.jwt;

import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;

import java.time.Duration;

/// A factory that produces
public interface JWTFactory {

    String generateToken(UserDetailsDto dto, JWTPurpose purpose, JWTClaim claim, Duration duration);

}
