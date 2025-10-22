package io.github.naomimyselfandi.xanaduwars.auth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;

import java.util.Optional;

/// A service that validates JWTs.
interface JWTValidator {

    /// Validate a JWT. If the JWT is valid, the return value provides access to
    /// the encoded information.
    Optional<DecodedJWT> validate(String token, JWTPurpose purpose);

}
