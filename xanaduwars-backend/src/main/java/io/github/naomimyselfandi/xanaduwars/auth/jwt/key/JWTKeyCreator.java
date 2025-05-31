package io.github.naomimyselfandi.xanaduwars.auth.jwt.key;

import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTPurpose;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Base64;
import java.util.Random;
import java.util.function.BiFunction;

@Slf4j
@RequiredArgsConstructor
@EqualsAndHashCode // For testing
class JWTKeyCreator implements BiFunction<JWTPurpose, Instant, JWTKey> {

    private final Random random; // Actually a SecureRandom outside of tests
    private final JWTKeyRepository jwtKeyRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public JWTKey apply(JWTPurpose purpose, Instant expiry) {
        jwtKeyRepository.awaitTransactionalLock();
        return jwtKeyRepository.findExistingKey(purpose, expiry).orElseGet(() -> create(purpose, expiry));
    }

    private JWTKey create(JWTPurpose purpose, Instant expiry) {
        log.info("Creating {} JWT key.", purpose);
        var secret = createRandomSecret();
        var key = new JWTKey().encodedSecret(secret).purpose(purpose).expiry(expiry);
        return jwtKeyRepository.save(key);
    }

    private String createRandomSecret() {
        var secret = new byte[32]; // 256-bit key for HMAC-SHA256
        random.nextBytes(secret);
        return Base64.getEncoder().encodeToString(secret);
    }

}
