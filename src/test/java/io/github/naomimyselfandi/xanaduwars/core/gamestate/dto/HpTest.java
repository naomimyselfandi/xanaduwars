package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Hp;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("deprecation")
@ExtendWith(SeededRandomExtension.class)
class HpTest {

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    void constructor_EnforcesBoundaries(int ordinal) {
        assertThatThrownBy(() -> new Hp(ordinal)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            -1,0
            0,0
            10,10
            42,42
            90,90
            100,100
            101,100
            """)
    void withOrdinal(int ordinal, int expected, SeededRng random) {
        var hp = random.<Hp>get();
        assertThat(hp.withOrdinal(ordinal)).isEqualTo(new Hp(expected));
    }

    @Test
    void testToString(SeededRng random) {
        var hp = random.<Hp>get();
        assertThat(hp).hasToString(String.valueOf(hp.ordinal()));
    }

    @Test
    void json(SeededRng random) {
        var hp = random.<Hp>get();
        TestUtils.assertJson(hp, String.valueOf(hp.ordinal()));
    }

}
