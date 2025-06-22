package io.github.naomimyselfandi.xanaduwars.auth.jwt;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.key.JWTKeyService;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.key.SecretKeyWithId;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SuppressWarnings("SpellCheckingInspection")
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class JWTValidatorImplTest {

    private static final String SECRET = "NZ1BuveK/g3hu+euKMBFDOQ8CE9Luyvxg53uRm2FLLU=";

    private UUID keyId;

    private UserDetailsDto dto;

    private JWTPurpose purpose;

    private JWTClaim claim;

    private Duration duration;

    @Mock
    private SecretKey secretKey;

    @Mock
    private Clock clock;

    @Mock
    private JWTKeyService jwtKeyService;

    private JWTFactoryImpl factory;

    @InjectMocks
    private JWTValidatorImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        keyId = random.nextUUID();
        dto = new UserDetailsDto();
        dto.setId(random.get());
        var now = Instant.now().minusSeconds(random.nextInt(1, 256));
        duration = Duration.ofMinutes(random.nextInt(1, 100));
        var bonusDuration = Duration.ofMinutes(random.nextInt(1, 100));
        var expiry = now.plus(duration).plus(bonusDuration);
        purpose = random.pick(JWTPurpose.values());
        var foo = random.nextUUID().toString();
        var bar = random.nextUUID().toString();
        claim = () -> Map.of(foo, bar);
        when(clock.instant()).thenReturn(now);
        when(secretKey.getEncoded()).thenReturn(SECRET.getBytes());
        when(jwtKeyService.getForSigning(purpose, expiry)).thenReturn(new SecretKeyWithId(secretKey, keyId));
        factory = new JWTFactoryImpl(bonusDuration, clock, jwtKeyService);
    }

    @RepeatedTest(3)
    void generateAccessToken() {
        when(jwtKeyService.getForValidation(purpose, keyId)).thenReturn(Optional.of(secretKey));
        var token = factory.generateToken(dto, purpose, claim, duration);
        assertThat(fixture.validateToken(token, purpose, claim))
                .hasValueSatisfying(jwt -> assertThat(jwt.getSubject()).isEqualTo(dto.getId().toString()));
    }

    @Test
    void generateAccessToken_WhenTheKeyHasExpired_ThenFails() {
        when(jwtKeyService.getForValidation(purpose, keyId)).thenReturn(Optional.empty());
        var token = factory.generateToken(dto, purpose, claim, duration);
        assertThat(fixture.validateToken(token, purpose, claim)).isEmpty();
    }

    @Test
    void generateAccessToken_WhenTheTokenIsWrong_ThenFails() {
        when(jwtKeyService.getForValidation(purpose, keyId)).thenReturn(Optional.of(secretKey));
        var token = factory.generateToken(dto, purpose, claim, duration);
        assertThat(fixture.validateToken(token + "z", purpose, claim)).isEmpty();
    }

    @Test
    void generateAccessToken_WhenThePurposeIsWrong_ThenFails(SeededRng random) {
        JWTPurpose wrongPurpose;
        do {
            wrongPurpose = random.pick(JWTPurpose.values());
        } while (purpose == wrongPurpose);
        when(jwtKeyService.getForValidation(wrongPurpose, keyId)).thenReturn(Optional.of(secretKey));
        var token = factory.generateToken(dto, purpose, claim, duration);
        assertThat(fixture.validateToken(token, wrongPurpose, claim)).isEmpty();
    }

    @Test
    void generateAccessToken_WhenTheClaimIsWrong_ThenFails(SeededRng random) {
        JWTClaim wrongClaim = () -> Map.of(random.nextUUID().toString(), random.nextUUID().toString());
        when(jwtKeyService.getForValidation(purpose, keyId)).thenReturn(Optional.of(secretKey));
        var token = factory.generateToken(dto, purpose, claim, duration);
        assertThat(fixture.validateToken(token, purpose, wrongClaim)).isEmpty();
    }

}
