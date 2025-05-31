package io.github.naomimyselfandi.xanaduwars.auth.jwt.key;

import javax.crypto.SecretKey;
import java.util.UUID;

/// A secret key with a unique ID that can be used in JWT headers.
public record SecretKeyWithId(SecretKey key, UUID id) {}
