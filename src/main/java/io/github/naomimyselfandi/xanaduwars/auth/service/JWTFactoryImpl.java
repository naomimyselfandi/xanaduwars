package io.github.naomimyselfandi.xanaduwars.auth.service;

import com.auth0.jwt.algorithms.Algorithm;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWT;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Date;

@Service
@RequiredArgsConstructor
class JWTFactoryImpl implements JWTFactory {

    private final Clock clock;
    private final JWTKeyService jwtKeyService;
    private final AuthConfiguration authConfiguration;
    private final JWTDurationService jwtDurationService;

    @Override
    public JWT create(UserDetailsDto dto, JWTPurpose purpose) {
        var now = clock.instant();
        var duration = jwtDurationService.getDuration(dto, purpose);
        var expiry = now.plus(duration).plus(authConfiguration.getJwtKeyBonusDuration());
        var keyWithId = jwtKeyService.getForSigning(purpose, expiry);
        var algorithm = Algorithm.HMAC256(keyWithId.key().getEncoded());
        var builder = com.auth0.jwt.JWT
                .create()
                .withKeyId(keyWithId.id().toString())
                .withSubject(dto.id().toString())
                .withClaim(JWTPurpose.CLAIM_KEY, purpose.name())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry));
        var token = builder.sign(algorithm);
        return new JWT(token, duration);
    }

}
