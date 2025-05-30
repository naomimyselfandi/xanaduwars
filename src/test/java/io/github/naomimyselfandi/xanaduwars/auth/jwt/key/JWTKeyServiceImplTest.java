package io.github.naomimyselfandi.xanaduwars.auth.jwt.key;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class JWTKeyServiceImplTest {

    @SuppressWarnings("SpellCheckingInspection")
    private static final String SECRET = "NZ1BuveK/g3hu+euKMBFDOQ8CE9Luyvxg53uRm2FLLU=";

    private static final SecretKey SECRET_KEY;

    static {
        var key = Base64.getDecoder().decode(SECRET);
        SECRET_KEY = new SecretKeySpec(key, 0, key.length, "HmacSHA256");
    }

    @Mock
    private JWTKeyRepository jwtKeyRepository;

    @Mock
    private BiFunction<JWTPurpose, Instant, JWTKey> creator;

    @Mock
    private Clock clock;

    private Instant instant;

    private JWTKeyServiceImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        instant = Instant.ofEpochMilli(random.nextLong());
        fixture = new JWTKeyServiceImpl(jwtKeyRepository, clock, creator);
    }

    @EnumSource
    @ParameterizedTest
    void getForSigning(JWTPurpose purpose, SeededRng random) {
        var id = random.nextUUID();
        var key = new JWTKey().id(id).encodedSecret(SECRET);
        when(jwtKeyRepository.findExistingKey(purpose, instant)).thenReturn(Optional.of(key));
        assertThat(fixture.getForSigning(purpose, instant)).isEqualTo(new SecretKeyWithId(SECRET_KEY, id));
    }

    @EnumSource
    @ParameterizedTest
    void getForSigning_CreatesANewKeyIfNeeded(JWTPurpose purpose, SeededRng random) {
        var id = random.nextUUID();
        var key = new JWTKey().id(id).encodedSecret(SECRET);
        when(creator.apply(purpose, instant)).thenReturn(key);
        assertThat(fixture.getForSigning(purpose, instant)).isEqualTo(new SecretKeyWithId(SECRET_KEY, id));
    }

    @EnumSource
    @ParameterizedTest
    void getForValidation(JWTPurpose purpose, SeededRng random) {
        var id = random.nextUUID();
        var key = new JWTKey().encodedSecret(SECRET);
        when(clock.instant()).thenReturn(instant);
        when(jwtKeyRepository.findExistingKey(id, purpose, instant)).thenReturn(Optional.of(key));
        assertThat(fixture.getForValidation(purpose, id)).contains(SECRET_KEY);
    }

    @Test
    void revokeAllJWTKeys() {
        fixture.revokeAllJWTKeys();
        verify(jwtKeyRepository).deleteAll();
    }

    @Test
    void deleteExpiredKeys() {
        when(clock.instant()).thenReturn(instant);
        fixture.deleteExpiredKeys();
        verify(jwtKeyRepository).deleteByExpiryBefore(instant);
    }

}
