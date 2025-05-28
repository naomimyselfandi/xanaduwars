package io.github.naomimyselfandi.xanaduwars.gameplay.common;

import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class UnitIdTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 255})
    void json(int intValue) {
        TestUtils.assertJson(new UnitId(intValue), String.valueOf(intValue));
    }

    @Test
    void compareTo() {
        TestUtils.assertSortOrder(new UnitId(0), new UnitId(1), new UnitId(2));
    }

    @RepeatedTest(2)
    void testToString(SeededRng random) {
        var i = random.nextInt(255);
        assertThat(new UnitId(i)).hasToString("Unit(%d)", i);
    }

}
