package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.auth.entity.JWTKey;

import java.time.Instant;

/// A factory that produces JWT signing keys.
interface JWTKeyFactory {

    /// Get a JWT signing key. This will use an existing signing key if one
    /// exists and is valid until the given expiry (or later). A new key is
    /// created otherwise.
    JWTKey get(JWTPurpose purpose, Instant expiry);

}
