package io.github.naomimyselfandi.xanaduwars.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.key.JWTKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
class JWTValidatorImpl implements JWTValidator {

    private final JWTKeyService jwtKeyService;

    @Override
    public Optional<DecodedJWT> validateToken(String token, JWTPurpose purpose, JWTClaim claim) {
        try {
            var decoded = JWT.decode(token);
            var keyId = UUID.fromString(decoded.getKeyId());
            var key = jwtKeyService.getForValidation(purpose, keyId);
            if (key.isEmpty()) return Optional.empty();
            var algorithm = Algorithm.HMAC256(key.get().getEncoded());
            var builder = JWT.require(algorithm).withClaim(JWTPurpose.CLAIM_KEY, purpose.name());
            claim.claims().forEach(builder::withClaim);
            var result = builder.build().verify(token);
            return Optional.of(result);
        } catch (JWTVerificationException | IllegalArgumentException e) {
            log.debug("JWT verification failed", e);
            return Optional.empty();
        }
    }

}
