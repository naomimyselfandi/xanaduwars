package io.github.naomimyselfandi.xanaduwars.auth.jwt;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class JWTDurationServiceImplTest {

    private UserDetailsDto dto;

    private Duration accessToken, shortRefreshToken, longRefreshToken, magicLink;

    private JWTDurationServiceImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        dto = new UserDetailsDto();
        fixture = new JWTDurationServiceImpl(
                accessToken = Duration.ofHours(random.nextInt(1, 10)),
                shortRefreshToken = Duration.ofHours(random.nextInt(1, 10)),
                longRefreshToken = Duration.ofHours(random.nextInt(100, 400)),
                magicLink = Duration.ofHours(random.nextInt(1, 10))
        );
    }

    @EnumSource
    @ParameterizedTest
    void duration_AccessToken(JWTDurationService.RememberMe rememberMe) {
        assertThat(fixture.duration(dto, JWTPurpose.ACCESS_TOKEN, rememberMe)).isEqualTo(accessToken);
    }

    @EnumSource
    @ParameterizedTest
    void duration_RefreshToken(JWTDurationService.RememberMe rememberMe) {
        var expected = switch (rememberMe) {
            case YES -> longRefreshToken;
            case NO -> shortRefreshToken;
        };
        assertThat(fixture.duration(dto, JWTPurpose.REFRESH_TOKEN, rememberMe)).isEqualTo(expected);
    }

    @EnumSource
    @ParameterizedTest
    void duration_MagicLink(JWTDurationService.RememberMe rememberMe) {
        assertThat(fixture.duration(dto, JWTPurpose.MAGIC_LINK, rememberMe)).isEqualTo(magicLink);
    }

}
