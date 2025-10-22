package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.auth.entity.JWTKey;
import io.github.naomimyselfandi.xanaduwars.auth.entity.JWTKeyRepository;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private JWTKeyFactory jwtKeyFactory;

    @Mock
    private Clock clock;

    private Instant instant;

    @InjectMocks
    private JWTKeyServiceImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        instant = Instant.ofEpochMilli(random.nextLong());
    }

    @EnumSource
    @ParameterizedTest
    void getForSigning(JWTPurpose purpose, SeededRng random) {
        var id = random.<Id<JWTKey>>get();
        var key = new JWTKey().setId(id).setEncodedSecret(SECRET);
        when(jwtKeyRepository.findExistingKey(purpose, instant)).thenReturn(Optional.of(key));
        assertThat(fixture.getForSigning(purpose, instant)).isEqualTo(new JWTKeyService.SecretKeyWithId(SECRET_KEY, id.id()));
    }

    @EnumSource
    @ParameterizedTest
    void getForSigning_CreatesANewKeyIfNeeded(JWTPurpose purpose, SeededRng random) {
        var id = random.<Id<JWTKey>>get();
        var key = new JWTKey().setId(id).setEncodedSecret(SECRET);
        when(jwtKeyFactory.get(purpose, instant)).thenReturn(key);
        assertThat(fixture.getForSigning(purpose, instant)).isEqualTo(new JWTKeyService.SecretKeyWithId(SECRET_KEY, id.id()));
    }

    @EnumSource
    @ParameterizedTest
    void getForValidation(JWTPurpose purpose, SeededRng random) {
        var id = random.<Id<JWTKey>>get();
        var key = new JWTKey().setEncodedSecret(SECRET);
        when(clock.instant()).thenReturn(instant);
        when(jwtKeyRepository.findExistingKey(id, purpose, instant)).thenReturn(Optional.of(key));
        assertThat(fixture.getForValidation(purpose, id)).contains(SECRET_KEY);
    }

    @Test
    void deleteExpiredKeys() {
        when(clock.instant()).thenReturn(instant);
        fixture.deleteExpiredKeys();
        verify(jwtKeyRepository).deleteByExpiryBefore(instant);
    }

}
