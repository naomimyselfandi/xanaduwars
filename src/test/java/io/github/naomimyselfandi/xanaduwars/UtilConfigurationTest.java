package io.github.naomimyselfandi.xanaduwars;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

class UtilConfigurationTest {

    private final UtilConfiguration fixture = new UtilConfiguration();

    @Test
    void instantSupplier() {
        var expected = Clock.systemUTC().instant();
        var actual = fixture.clock().instant();
        assertThat(actual).isCloseTo(expected, within(Duration.ofSeconds(1)));
    }

    @Test
    void uuidSupplier() {
        var supplier = fixture.uuidSupplier();
        var repetitions = 5000;
        assertThat(IntStream.range(0, repetitions).mapToObj(_ -> supplier.get()).distinct()).hasSize(repetitions);
    }

    @Test
    void secureRandom() {
        assertThat(fixture.secureRandom()).isExactlyInstanceOf(SecureRandom.class);
    }

}
