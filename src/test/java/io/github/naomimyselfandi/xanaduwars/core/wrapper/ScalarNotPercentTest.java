package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class ScalarNotPercentTest {

    @ParameterizedTest
    @ValueSource(doubles = {0, 0.25, 0.5, 0.75, 1, Double.NaN})
    void checksRange(double doubleValue) {
        assertThatThrownBy(() -> new ScalarNotPercent(doubleValue)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            1.001,1
            -0.001,0
            Infinity,1
            -Infinity,0
            """)
    void clamp(double doubleValue, double expected) {
        assertThat(new ScalarNotPercent(doubleValue).clamp()).isEqualTo(new Percent(expected));
    }

}
