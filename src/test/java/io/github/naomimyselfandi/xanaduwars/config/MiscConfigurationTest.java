package io.github.naomimyselfandi.xanaduwars.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

class MiscConfigurationTest {

    private final MiscConfiguration fixture = new MiscConfiguration();

    @Test
    void instantSupplier() {
        var expected = Clock.systemUTC().instant();
        var actual = fixture.clock().instant();
        assertThat(actual).isCloseTo(expected, within(Duration.ofSeconds(1)));
    }

    @Test
    void uuidSupplier() {
        var repetitions = 5000;
        var supplier = fixture.uuidSupplier();
        assertThat(IntStream.range(0, repetitions).mapToObj(_ -> supplier.get()).distinct()).hasSize(repetitions);
    }

    @Test
    void secureRandom() {
        assertThat(fixture.secureRandom()).isExactlyInstanceOf(SecureRandom.class);
    }

    @Test
    void passwordEncoder() {
        assertThat(fixture.passwordEncoder()).isExactlyInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void securityContextSupplier() {
        assertThat(fixture.securityContextSupplier().get()).isEqualTo(SecurityContextHolder.getContext());
    }

}
