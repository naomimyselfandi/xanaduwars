package io.github.naomimyselfandi.xanaduwars.auth.jwt;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;

import java.time.Duration;

/// A factory that produces
public interface JWTFactory {

    String generateToken(Account account, JWTPurpose purpose, JWTClaim claim, Duration duration);

}
