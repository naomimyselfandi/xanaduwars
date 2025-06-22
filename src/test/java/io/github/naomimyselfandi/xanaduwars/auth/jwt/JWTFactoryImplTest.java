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
        dto.setId(random.get());
        var now = Instant.ofEpochMilli(random.nextLong());
        duration = Duration.ofMinutes(random.nextInt(1, 100));
        var bonusDuration = Duration.ofMinutes(random.nextInt(1, 100));
        var expiry = now.plus(duration).plus(bonusDuration);
        purpose = random.pick(JWTPurpose.values());
        var foo = random.nextUUID().toString();
        var bar = random.nextUUID().toString();
        claim = () -> Map.of(foo, bar);
        when(clock.instant()).thenReturn(now);
        when(secretKey.getEncoded()).thenReturn(SECRET.getBytes());
        when(jwtKeyService.getForSigning(purpose, expiry)).thenReturn(new SecretKeyWithId(secretKey, dto.getId().id()));
        fixture = new JWTFactoryImpl(bonusDuration, clock, jwtKeyService);
    }

    @RepeatedTest(3)
    void generateToken(RepetitionInfo repetitionInfo) {
        var expected = """
                eyJraWQiOiI4YzFhZTRiOS0xYzRlLWM1MjctNzJiMy1hN2NhZGI1Mzk1ODUiLCJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI4YzFhZTRiOS0xYzRlLWM1MjctNzJiMy1hN2NhZGI1Mzk1ODUiLCJwdXJwb3NlIjoiUkVGUkVTSF9UT0tFTiIsImlhdCI6LTQ0NTY1OTY5MjUwNzA2NDksImV4cCI6LTQ0NTY1OTY5MjUwNjI3MjksIjEzMjU5YmRjLTkwZDQtMDk5YS1kNjczLTRhZTg0YTcwZmExMCI6IjU1NDljZTlkLTE5ZTMtYWMwOC02NzA0LTFjZDcwMTY4YjkzMCJ9.3PEBGq9L8J9b7ZzImoIjx70-2Bk7u-LUR-zobQFt21g
                eyJraWQiOiI2NWI4MTIwYy03M2E1LTg2YTgtMzZkNC02OTMyZGRkNTFlY2QiLCJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2NWI4MTIwYy03M2E1LTg2YTgtMzZkNC02OTMyZGRkNTFlY2QiLCJwdXJwb3NlIjoiTUFHSUNfTElOSyIsImlhdCI6LTY3ODIwNTg5OTEzNjcxNjQsImV4cCI6LTY3ODIwNTg5OTEzNjI5NjQsIjFjNTQ4ZDRiLWMxMTctYzIyNi0zMTAzLWQxMTBiMTJiMjQ5MiI6IjRiYjE1NzI2LWFjZmUtN2Q3MC00M2UwLWM0NGYwMzI1MGU4YiJ9.wZhSZzQyGeJxF4NUxCUCPIkNGHUUH3KgsC86XoHsSJ0
                eyJraWQiOiJmNWU0YmM5YS0zNWRmLTdjMWUtNmU4Mi1jNmQwOTY4YWMwM2MiLCJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJmNWU0YmM5YS0zNWRmLTdjMWUtNmU4Mi1jNmQwOTY4YWMwM2MiLCJwdXJwb3NlIjoiTUFHSUNfTElOSyIsImlhdCI6MTczOTg1NjE4OTQwNjgzMywiZXhwIjoxNzM5ODU2MTg5NDEzNzkzLCIzOTg1MjY1Yi00NDI0LTkxYWEtMmRlMi0xZjhkOGMwODM1YjIiOiIwMTE4ZDY4Yi1kZjEwLTgzZDEtNTYwZS04YzgxYjZmMDhiM2UifQ.cxYVffm1kM8yHIBiPkQqo5kyhBWzg-tojp1CnugbWok
                """
                .lines()
                .skip(repetitionInfo.getCurrentRepetition() - 1)
                .findFirst()
                .orElseThrow();
        assertThat(fixture.generateToken(dto, purpose, claim, duration)).isEqualTo(expected);
    }

}
