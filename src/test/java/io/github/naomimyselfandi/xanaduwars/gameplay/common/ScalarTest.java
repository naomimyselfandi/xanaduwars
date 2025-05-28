package io.github.naomimyselfandi.xanaduwars.gameplay.common;

import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class ScalarTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,true
            0.25,true
            0.5,true
            0.75,true
            1,true
            1.001,false
            -0.001,false
            Infinity,false
            -Infinity,false
            """)
    void withDoubleValue(double doubleValue, boolean percent) {
        if (percent) {
            assertThat(Scalar.withDoubleValue(doubleValue)).isEqualTo(Percent.withDoubleValue(doubleValue));
        } else {
            assertThat(Scalar.withDoubleValue(doubleValue)).isEqualTo(new ScalarNotPercent(doubleValue));
        }
    }

    @Test
    void compareTo() {
        TestUtils.assertSortOrder(
                Scalar.withDoubleValue(Double.NEGATIVE_INFINITY),
                Scalar.withDoubleValue(-1000),
                Scalar.withDoubleValue(-100),
                Scalar.withDoubleValue(-10),
                Scalar.withDoubleValue(-1),
                Scalar.withDoubleValue(-0.1),
                Scalar.withDoubleValue(-0.01),
                Scalar.withDoubleValue(0),
                Scalar.withDoubleValue(0.01),
                Scalar.withDoubleValue(0.1),
                Scalar.withDoubleValue(1),
                Scalar.withDoubleValue(10),
                Scalar.withDoubleValue(100),
                Scalar.withDoubleValue(1000),
                Scalar.withDoubleValue(Double.POSITIVE_INFINITY)
        );
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.8, 1.2})
    void json(double value) {
        TestUtils.assertJson(Scalar.withDoubleValue(value), String.valueOf(value));
    }

}
