package io.github.naomimyselfandi.xanaduwars.auth.jwt.key;

import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTPurpose;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/// A factory that fetches JWT keys.
public interface JWTKeyService {

    /// Get a JWT key for the given purpose. The key's expiry will be at least
    /// the given duration in the future, and its UUID is included
    SecretKeyWithId getForSigning(JWTPurpose purpose, Instant expiry);

    /// Get an existing JWT key by its purpose and ID. If the key exists but
    /// has expired, this returns an empty [Optional] as if it did not exist.
    Optional<SecretKey> getForValidation(JWTPurpose purpose, UUID id);

    /// Revoke all existing JWT keys in the event of a security breach.
    void revokeAllJWTKeys();

}
