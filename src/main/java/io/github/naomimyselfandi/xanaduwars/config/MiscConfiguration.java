package io.github.naomimyselfandi.xanaduwars.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.UUID;
import java.util.function.Supplier;

@Configuration
class MiscConfiguration {

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

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    Supplier<SecurityContext> securityContextSupplier() {
        return SecurityContextHolder::getContext;
    }

}
