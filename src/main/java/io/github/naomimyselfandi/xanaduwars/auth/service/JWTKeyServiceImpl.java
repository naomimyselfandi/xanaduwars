package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.auth.entity.JWTKey;
import io.github.naomimyselfandi.xanaduwars.auth.entity.JWTKeyRepository;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
class JWTKeyServiceImpl implements JWTKeyService {

    private final JWTKeyRepository jwtKeyRepository;
    private final JWTKeyFactory jwtKeyFactory;
    private final Clock clock;

    @Override
    public SecretKeyWithId getForSigning(JWTPurpose purpose, Instant expiry) {
        var jwtKey = jwtKeyRepository
                .findExistingKey(purpose, expiry)
                .orElseGet(() -> jwtKeyFactory.get(purpose, expiry));
        return new SecretKeyWithId(toSecretKey(jwtKey), jwtKey.getId().id());
    }

    @Override
    public Optional<SecretKey> getForValidation(JWTPurpose purpose, Id<JWTKey> id) {
        return jwtKeyRepository.findExistingKey(id, purpose, clock.instant()).map(JWTKeyServiceImpl::toSecretKey);
    }

    @Transactional
    @PostConstruct
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    public void deleteExpiredKeys() {
        jwtKeyRepository.deleteByExpiryBefore(clock.instant());
    }

    private static SecretKey toSecretKey(JWTKey jwtKey) {
        var key = Base64.getDecoder().decode(jwtKey.getEncodedSecret());
        return new SecretKeySpec(key, 0, key.length, "HmacSHA256");
    }

}
