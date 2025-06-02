package io.github.naomimyselfandi.xanaduwars.auth.jwt;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.key.JWTKeyService;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.key.SecretKeyWithId;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SuppressWarnings("SpellCheckingInspection")
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class JWTFactoryImplTest {

    private static final String SECRET = "NZ1BuveK/g3hu+euKMBFDOQ8CE9Luyvxg53uRm2FLLU=";

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

    private JWTFactoryImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        dto = new UserDetailsDto();
        dto.setId(random.nextAccountId());
        var now = Instant.ofEpochMilli(random.nextLong());
        duration = Duration.ofMinutes(random.nextInt(1, 100));
        var bonusDuration = Duration.ofMinutes(random.nextInt(1, 100));
        var expiry = now.plus(duration).plus(bonusDuration);
        purpose = random.pick(JWTPurpose.values());
        var foo = random.nextUnitId().toString();
        var bar = random.nextUnitId().toString();
        claim = () -> Map.of(foo, bar);
        when(clock.instant()).thenReturn(now);
        when(secretKey.getEncoded()).thenReturn(SECRET.getBytes());
        when(jwtKeyService.getForSigning(purpose, expiry)).thenReturn(new SecretKeyWithId(secretKey, dto.getId().id()));
        fixture = new JWTFactoryImpl(bonusDuration, clock, jwtKeyService);
    }

    @RepeatedTest(3)
    void generateToken(RepetitionInfo repetitionInfo) {
        var expected = """
                eyJraWQiOiI0YTdiZTFhZi04YzFhLWU0YjktMWM0ZS1jNTI3NzJiM2E3Y2IiLCJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI0YTdiZTFhZi04YzFhLWU0YjktMWM0ZS1jNTI3NzJiM2E3Y2IiLCJwdXJwb3NlIjoiUkVGUkVTSF9UT0tFTiIsImlhdCI6LTI2NDI2MDQxNTg5MTA3OTIsImV4cCI6LTI2NDI2MDQxNTg5MDEzMTIsIlVuaXQoMTcyMjg2NDc4MikiOiJVbml0KDMyMTIzMTgzNykifQ.qqICDaWu_-BNLbvHbX3-8kAbjQXUODuCu_6rYp3q0Qw
                eyJraWQiOiIyZjhhYjFlYS02NWI4LTEyMGMtNzNhNS04NmE4MzZkNDY5MzMiLCJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyZjhhYjFlYS02NWI4LTEyMGMtNzNhNS04NmE4MzZkNDY5MzMiLCJwdXJwb3NlIjoiQUNDRVNTX1RPS0VOIiwiaWF0IjotMjQ2MjAyNzc1NzA1MDA1MCwiZXhwIjotMjQ2MjAyNzc1NzA0MTgzMCwiVW5pdCgxODI3NjcyODgxKSI6IlVuaXQoNDc1MzAzMjQ0KSJ9.f3cydv2mlGDWZo_bjvX3NlZMe2i0YH61b2lWo-zHsw4
                eyJraWQiOiIwZmE2ZWU5Ny1mNWU0LWJjOWEtMzVkZi03YzFlNmU4MmM2ZDEiLCJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIwZmE2ZWU5Ny1mNWU0LWJjOWEtMzVkZi03YzFlNmU4MmM2ZDEiLCJwdXJwb3NlIjoiUkVGUkVTSF9UT0tFTiIsImlhdCI6LTc1OTkwNTAwNTY4OTg2MjgsImV4cCI6LTc1OTkwNTAwNTY4ODk5ODgsIlVuaXQoMTQzMjgyMSkiOiJVbml0KDk2NTAyNzQxOSkifQ.afA41la0FManAEtodgEvSA0klnDHRjSwvSQwYvfFD9Y
                """
                .lines()
                .skip(repetitionInfo.getCurrentRepetition() - 1)
                .findFirst()
                .orElseThrow();
        assertThat(fixture.generateToken(dto, purpose, claim, duration)).isEqualTo(expected);
    }

}
