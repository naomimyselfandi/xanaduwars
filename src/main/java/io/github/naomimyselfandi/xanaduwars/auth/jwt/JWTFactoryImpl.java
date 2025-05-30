package io.github.naomimyselfandi.xanaduwars.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.key.JWTKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
class JWTFactoryImpl implements JWTFactory {

    @Value("${xanadu.auth.jwt.signing-key-bonus-duration}")
    private final Duration bonusDuration;

    private final Clock clock;
    private final JWTKeyService jwtKeyService;

    @Override
    public String generateToken(Account account, JWTPurpose purpose, JWTClaim claim, Duration duration) {
        var now = clock.instant();
        var expiry = now.plus(duration).plus(bonusDuration);
        var keyWithId = jwtKeyService.getForSigning(purpose, expiry);
        var algorithm = Algorithm.HMAC256(keyWithId.key().getEncoded());
        var builder = JWT
                .create()
                .withKeyId(keyWithId.id().toString())
                .withSubject(account.id().toString())
                .withClaim(JWTPurpose.CLAIM_KEY, purpose.name())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry));
        claim.claims().forEach(builder::withClaim);
        return builder.sign(algorithm);
    }

}
