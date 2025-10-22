package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWT;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SuppressWarnings("SpellCheckingInspection")
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class JWTFactoryImplTest {

    private static final String SECRET = "NZ1BuveK/g3hu+euKMBFDOQ8CE9Luyvxg53uRm2FLLU=";

    private UserDetailsDto dto;

    private JWTPurpose purpose;

    private Duration duration;

    @Mock
    private SecretKey secretKey;

    @Mock
    private Clock clock;

    @Mock
    private JWTKeyService jwtKeyService;

    @Mock
    private JWTDurationService jwtDurationService;

    private JWTFactoryImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        dto = random.get();
        var now = Instant.ofEpochMilli(random.nextLong());
        duration = Duration.ofMinutes(random.nextInt(1, 100));
        var bonusDuration = Duration.ofMinutes(random.nextInt(1, 100));
        var expiry = now.plus(duration).plus(bonusDuration);
        purpose = random.pick(JWTPurpose.values());
        when(clock.instant()).thenReturn(now);
        when(secretKey.getEncoded()).thenReturn(SECRET.getBytes());
        when(jwtKeyService.getForSigning(purpose, expiry)).thenReturn(new JWTKeyService.SecretKeyWithId(secretKey, dto.id().id()));
        when(jwtDurationService.getDuration(dto, purpose)).thenReturn(duration);
        var configuration = new AuthConfiguration().setJwtKeyBonusDuration(bonusDuration);
        fixture = new JWTFactoryImpl(clock, jwtKeyService, configuration, jwtDurationService);
    }

    @RepeatedTest(3)
    void create(RepetitionInfo repetitionInfo) {
        var token = """
                eyJraWQiOiIxYzRlYzUyNy03MmIzLWE3Y2ItZGI1My05NTg0YzIyNmZjYjUiLCJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxYzRlYzUyNy03MmIzLWE3Y2ItZGI1My05NTg0YzIyNmZjYjUiLCJwdXJwb3NlIjoiUEFTU1dPUkRfUkVTRVQiLCJpYXQiOjU4ODM0NDA1MTc2NTg2NDMsImV4cCI6NTg4MzQ0MDUxNzY2OTYyM30.qoT2keNwhVCuC0ZDC97_rGeLUjoYCgO5iIcL4IFrlCQ
                eyJraWQiOiI3M2E1ODZhOC0zNmQ0LTY5MzMtZGRkNS0xZWNjYTFlMTQ5NDgiLCJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3M2E1ODZhOC0zNmQ0LTY5MzMtZGRkNS0xZWNjYTFlMTQ5NDgiLCJwdXJwb3NlIjoiQUNDRVNTX1RPS0VOIiwiaWF0Ijo3Nzc4MzQ5ODUzOTk2MzM2LCJleHAiOjc3NzgzNDk4NTQwMDA4OTZ9._3QgSmgF_cr3uGOyLGTBp4p0pzLqYGupbHNTobDV-GY
                eyJraWQiOiIzNWRmN2MxZS02ZTgyLWM2ZDEtOTY4YS1jMDNjMTgyNTM1ZmMiLCJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIzNWRmN2MxZS02ZTgyLWM2ZDEtOTY4YS1jMDNjMTgyNTM1ZmMiLCJwdXJwb3NlIjoiUkVGUkVTSF9UT0tFTiIsImlhdCI6MzE0NzcwODg0NTA1NDY4NiwiZXhwIjozMTQ3NzA4ODQ1MDYwODY2fQ.DtOyAb5dxFuV2NtI40PLuE0pAbcuQ-XsJaAZCNIMNUY
                """.lines().toList().get(repetitionInfo.getCurrentRepetition() - 1);
        var expected = new JWT(token, duration);
        assertThat(fixture.create(dto, purpose)).isEqualTo(expected);
    }

}
