package io.github.naomimyselfandi.xanaduwars;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.UUID;
import java.util.function.Supplier;

@Configuration
class UtilConfiguration {

    @Bean(name = "systemUTC")
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    Supplier<UUID> uuidSupplier() {
        return UUID::randomUUID;
    }

    @Bean
    SecureRandom secureRandom() {
        return new SecureRandom();
    }

}
