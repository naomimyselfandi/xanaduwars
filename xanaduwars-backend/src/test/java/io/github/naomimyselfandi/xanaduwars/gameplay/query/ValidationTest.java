package io.github.naomimyselfandi.xanaduwars.gameplay.query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationTest {

    @Test
    void defaultValue() {
        assertThat(new Validation() {}.defaultValue()).isTrue();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,false
            false,true
            """)
    void shouldShortCircuit(boolean value, boolean expected) {
        assertThat(new Validation() {}.shouldShortCircuit(value)).isEqualTo(expected);
    }

}
