package io.github.naomimyselfandi.xanaduwars.email.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "xanadu.email")
class EmailConfiguration {

    private int batchSize;

    private Duration baseRetryInterval;

    private int maxAttempts;

}
