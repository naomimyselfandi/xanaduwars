package io.github.naomimyselfandi.xanaduwars.auth.jwt.key;

import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTPurpose;
import jakarta.annotation.PostConstruct;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

@Slf4j
@RequiredArgsConstructor
@EqualsAndHashCode // For testing
class JWTKeyServiceImpl implements JWTKeyService {

    private final JWTKeyRepository jwtKeyRepository;
    private final Clock clock;
    private final BiFunction<JWTPurpose, Instant, JWTKey> creator;

    @Override
    public SecretKeyWithId getForSigning(JWTPurpose purpose, Instant expiry) {
        var jwtKey = jwtKeyRepository
                .findExistingKey(purpose, expiry)
                .orElseGet(() -> creator.apply(purpose, expiry));
        return new SecretKeyWithId(toSecretKey(jwtKey), jwtKey.getId());
    }

    @Override
    public Optional<SecretKey> getForValidation(JWTPurpose purpose, UUID id) {
        return jwtKeyRepository.findExistingKey(id, purpose, clock.instant()).map(JWTKeyServiceImpl::toSecretKey);
    }

    @Override
    public void revokeAllJWTKeys() {
        // Not an error, but this should be logged LOUDLY.
        log.error("Revoking all JWT keys!");
        jwtKeyRepository.deleteAll();
    }

    @Transactional
    @PostConstruct
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    public void deleteExpiredKeys() {
        var deleted = jwtKeyRepository.deleteByExpiryBefore(clock.instant());
        log.info("Deleted {} expired JWT keys.", deleted);
    }

    private static SecretKey toSecretKey(JWTKey jwtKey) {
        var key = Base64.getDecoder().decode(jwtKey.getEncodedSecret());
        return new SecretKeySpec(key, 0, key.length, "HmacSHA256");
    }

}
