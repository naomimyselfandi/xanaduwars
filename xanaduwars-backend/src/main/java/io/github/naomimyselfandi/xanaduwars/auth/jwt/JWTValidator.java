package io.github.naomimyselfandi.xanaduwars.auth.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Optional;

/// A service that validates JWTs.
public interface JWTValidator {

    /// Validate a JWT. If the JWT is valid, the return value provides access to
    /// the encoded information.
    Optional<DecodedJWT> validateToken(String token, JWTPurpose purpose, JWTClaim claim);

}
