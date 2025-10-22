package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWT;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;

/// A factory that produces JWTs.
public interface JWTFactory {

    /// Generate a new JWT.
    JWT create(UserDetailsDto dto, JWTPurpose purpose);

}
