package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class TileIdTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0,-1000000
            10,20,-1020010
            0,999,-1999000
            999,000,-1000999
            999,999,-1999999
            """)
    void intValue(int x, int y, int expected) {
        assertThat(new TileId(x, y).intValue()).isEqualTo(expected);
    }

    @RepeatedTest(10)
    void withIntValue(SeededRandom random) {
        var x = random.nextInt(0, 1000);
        var y = random.nextInt(0, 1000);
        var tileId = new TileId(x, y);
        var intValue = tileId.intValue();
        assertThat(TileId.withIntValue(intValue)).isEqualTo(tileId);
    }

    @Test
    void compareTo() {
        TestUtils.assertSortOrder(
                new TileId(0, 0),
                new TileId(1, 0),
                new TileId(0, 1),
                new TileId(1, 1)
        );
    }

    @RepeatedTest(5)
    void testToString(SeededRandom random) {
        var x = random.nextInt(255);
        var y = random.nextInt(255);
        assertThat(new TileId(x, y)).hasToString("Tile(%d, %d)", x, y);
    }

}
