package io.github.naomimyselfandi.xanaduwars.auth.jwt;

/// The purpose for a JWT.
public enum JWTPurpose {

    /// A short-lived access token.
    ACCESS_TOKEN,

    /// A long-lived refresh token.
    REFRESH_TOKEN,

    /// A magic link with a variable duration.
    MAGIC_LINK;

    static final String CLAIM_KEY = "purpose";

}
