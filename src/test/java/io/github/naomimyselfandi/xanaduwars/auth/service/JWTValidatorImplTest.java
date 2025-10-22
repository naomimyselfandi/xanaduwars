package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.entity.JWTKey;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SuppressWarnings("SpellCheckingInspection")
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class JWTValidatorImplTest {

    private static final String SECRET = "NZ1BuveK/g3hu+euKMBFDOQ8CE9Luyvxg53uRm2FLLU=";

    private Id<JWTKey> keyId;

    private UserDetailsDto dto;

    private JWTPurpose purpose;

    @Mock
    private SecretKey secretKey;

    @Mock
    private Clock clock;

    @Mock
    private JWTKeyService jwtKeyService;

    @Mock
    private JWTDurationService jwtDurationService;

    private JWTFactoryImpl factory;

    @InjectMocks
    private JWTValidatorImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        keyId = random.get();
        dto = random.get();
        var now = Instant.now().minusSeconds(random.nextInt(1, 256));
        var duration = Duration.ofMinutes(random.nextInt(1, 100));
        var bonusDuration = Duration.ofMinutes(random.nextInt(1, 100));
        var expiry = now.plus(duration).plus(bonusDuration);
        purpose = random.pick(JWTPurpose.values());
        when(clock.instant()).thenReturn(now);
        when(secretKey.getEncoded()).thenReturn(SECRET.getBytes());
        when(jwtKeyService.getForSigning(purpose, expiry)).thenReturn(new JWTKeyService.SecretKeyWithId(secretKey, keyId.id()));
        when(jwtDurationService.getDuration(dto, purpose)).thenReturn(duration);
        var configuration = new AuthConfiguration().setJwtKeyBonusDuration(bonusDuration);
        factory = new JWTFactoryImpl(clock, jwtKeyService, configuration, jwtDurationService);
    }

    @RepeatedTest(3)
    void validate() {
        when(jwtKeyService.getForValidation(purpose, keyId)).thenReturn(Optional.of(secretKey));
        var token = factory.create(dto, purpose).token();
        assertThat(fixture.validate(token, purpose))
                .hasValueSatisfying(jwt -> assertThat(jwt.getSubject()).isEqualTo(dto.id().toString()));
    }

    @Test
    void validate_WhenTheKeyHasExpired_ThenFails() {
        when(jwtKeyService.getForValidation(purpose, keyId)).thenReturn(Optional.empty());
        var token = factory.create(dto, purpose).token();
        assertThat(fixture.validate(token, purpose)).isEmpty();
    }

    @Test
    void validate_WhenTheTokenIsWrong_ThenFails() {
        when(jwtKeyService.getForValidation(purpose, keyId)).thenReturn(Optional.of(secretKey));
        var token = factory.create(dto, purpose).token();
        assertThat(fixture.validate(token + "z", purpose)).isEmpty();
    }

    @Test
    void validate_WhenThePurposeIsWrong_ThenFails(SeededRng random) {
        JWTPurpose wrongPurpose;
        do {
            wrongPurpose = random.pick(JWTPurpose.values());
        } while (purpose == wrongPurpose);
        when(jwtKeyService.getForValidation(wrongPurpose, keyId)).thenReturn(Optional.of(secretKey));
        var token = factory.create(dto, purpose).token();
        assertThat(fixture.validate(token, wrongPurpose)).isEmpty();
    }

}
