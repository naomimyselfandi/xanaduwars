package io.github.naomimyselfandi.xanaduwars.auth.jwt.key;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.time.Clock;

@Configuration
class JWTKeyConfiguration {

    @Bean
    JWTKeyService jwtKeyService(SecureRandom secureRandom, JWTKeyRepository jwtKeyRepository, Clock clock) {
        return new JWTKeyServiceImpl(jwtKeyRepository, clock, new JWTKeyCreator(secureRandom, jwtKeyRepository));
    }

}
