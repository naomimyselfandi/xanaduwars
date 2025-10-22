package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class JWTDurationServiceImplTest {

    private Duration accessTokenDuration, refreshTokenShortDuration, refreshTokenLongDuration, passwordResetDuration;

    private JWTDurationServiceImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        accessTokenDuration = Duration.ofHours(random.nextInt(1, 10));
        refreshTokenShortDuration = Duration.ofHours(random.nextInt(1, 10));
        refreshTokenLongDuration = Duration.ofHours(random.nextInt(100, 400));
        passwordResetDuration = Duration.ofHours(random.nextInt(1, 10));
        var configuration = new AuthConfiguration()
                .setAccessTokenDuration(accessTokenDuration)
                .setRefreshTokenShortDuration(refreshTokenShortDuration)
                .setRefreshTokenLongDuration(refreshTokenLongDuration)
                .setPasswordResetDuration(passwordResetDuration);
        fixture = new JWTDurationServiceImpl(configuration);
    }

    @Test
    void getDuration_AccessToken(SeededRng random) {
        assertThat(fixture.getDuration(random.get(), JWTPurpose.ACCESS_TOKEN)).isEqualTo(accessTokenDuration);
    }

    @Test
    void getDuration_RefreshToken(SeededRng random) {
        var dto = random.<UserDetailsDto>get().toBuilder().rememberMe(false).build();
        assertThat(fixture.getDuration(dto, JWTPurpose.REFRESH_TOKEN)).isEqualTo(refreshTokenShortDuration);
    }

    @Test
    void getDuration_RefreshToken_RememberMe(SeededRng random) {
        var dto = random.<UserDetailsDto>get().toBuilder().rememberMe(true).build();
        assertThat(fixture.getDuration(dto, JWTPurpose.REFRESH_TOKEN)).isEqualTo(refreshTokenLongDuration);
    }

    @Test
    void getDuration_PasswordReset(SeededRng random) {
        assertThat(fixture.getDuration(random.get(), JWTPurpose.PASSWORD_RESET)).isEqualTo(passwordResetDuration);
    }

}
