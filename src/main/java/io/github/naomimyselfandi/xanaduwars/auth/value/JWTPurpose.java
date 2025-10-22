package io.github.naomimyselfandi.xanaduwars.auth.value;

/// The purpose for a JWT.
public enum JWTPurpose {

    /// A short-lived access token.
    ACCESS_TOKEN,

    /// A long-lived refresh token.
    REFRESH_TOKEN,

    /// A request to reset a password.
    PASSWORD_RESET;

    /// The claim key used to track a JWT's purpose.
    public static final String CLAIM_KEY = "purpose";

}
