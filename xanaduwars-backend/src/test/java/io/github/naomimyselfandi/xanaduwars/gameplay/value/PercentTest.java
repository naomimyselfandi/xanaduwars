package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class PercentTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0
            0.25,0.25
            0.5,0.5
            0.75,0.75
            1,1
            1.1,1
            -0.1,0
            """)
    void clamp(double value, double expected) {
        assertThat(Percent.clamp(value)).isEqualTo(new Percent(expected));
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1.01, -0.01})
    void withDoubleValue_ProhibitsOutOfRangeValues(double value) {
        assertThatThrownBy(() -> Percent.withDoubleValue(value)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, 0.25, 0.5, 0.75, 1})
    void withDoubleValue_AcceptsInRangeValues(double value) {
        assertThatCode(() -> Percent.withDoubleValue(value)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1.01, -0.01})
    void constructor_ProhibitsOutOfRangeValues(double value) {
        assertThatThrownBy(() -> new Percent(value)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, 0.25, 0.5, 0.75, 1})
    void constructor_AcceptsInRangeValues(double value) {
        assertThatCode(() -> new Percent(value)).doesNotThrowAnyException();
    }

    @RepeatedTest(10)
    @ExtendWith(SeededRandomExtension.class)
    void clamp(SeededRng random) {
        var percent = new Percent(random.nextDouble());
        assertThat(percent.clamp()).isSameAs(percent);
    }

    @Test
    void json() {
        TestUtils.assertJson(Percent.withDoubleValue(0.8), "0.8");
    }

}
