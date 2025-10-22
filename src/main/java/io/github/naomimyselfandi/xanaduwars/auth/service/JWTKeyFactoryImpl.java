package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.auth.entity.JWTKey;
import io.github.naomimyselfandi.xanaduwars.auth.entity.JWTKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Base64;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
class JWTKeyFactoryImpl implements JWTKeyFactory {

    @Qualifier("secureRandom")
    private final Random random;
    private final JWTKeyRepository jwtKeyRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public JWTKey get(JWTPurpose purpose, Instant expiry) {
        jwtKeyRepository.awaitTransactionalLock();
        return jwtKeyRepository.findExistingKey(purpose, expiry).orElseGet(() -> create(purpose, expiry));
    }

    private JWTKey create(JWTPurpose purpose, Instant expiry) {
        log.info("Creating {} JWT key.", purpose);
        var secret = createRandomSecret();
        var key = new JWTKey().setEncodedSecret(secret).setPurpose(purpose).setExpiry(expiry);
        return jwtKeyRepository.save(key);
    }

    private String createRandomSecret() {
        var secret = new byte[32]; // 256-bit key for HMAC-SHA256
        random.nextBytes(secret);
        return Base64.getEncoder().encodeToString(secret);
    }

}
