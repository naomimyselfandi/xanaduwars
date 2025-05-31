package io.github.naomimyselfandi.xanaduwars.auth.jwt;

import java.util.Map;

/// A set of claims that can be represented by a JWT.
public interface JWTClaim {

    /// An empty claim set.
    JWTClaim NONE = Map::of;

    /// The claims represented by the JWT.
    Map<String, String> claims();

}
