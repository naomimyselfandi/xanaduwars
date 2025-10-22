package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.auth.entity.JWTKey;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.util.Id;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/// A factory that fetches JWT keys.
interface JWTKeyService {

    /// A secret key with a unique ID, suitable for use in JWT headers.
    record SecretKeyWithId(SecretKey key, UUID id) {}

    /// Get a JWT key for the given purpose.
    SecretKeyWithId getForSigning(JWTPurpose purpose, Instant expiry);

    /// Get an existing JWT key by its purpose and ID. If the key exists but
    /// has expired, this returns an empty [Optional] as if it did not exist.
    Optional<SecretKey> getForValidation(JWTPurpose purpose, Id<JWTKey> id);

}
