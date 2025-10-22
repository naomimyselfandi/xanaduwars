package io.github.naomimyselfandi.xanaduwars.auth.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "xanadu.auth")
class AuthConfiguration {
    private Duration jwtKeyBonusDuration;
    private Duration accessTokenDuration;
    private Duration refreshTokenShortDuration;
    private Duration refreshTokenLongDuration;
    private Duration passwordResetDuration;
}
