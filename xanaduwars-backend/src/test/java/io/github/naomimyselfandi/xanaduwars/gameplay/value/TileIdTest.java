package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class TileIdTest {

    @Test
    void compareTo() {
        TestUtils.assertSortOrder(
                new TileId(0, 0),
                new TileId(1, 0),
                new TileId(0, 1),
                new TileId(1, 1)
        );
    }

    @RepeatedTest(2)
    void testToString(SeededRng random) {
        var x = random.nextInt(255);
        var y = random.nextInt(255);
        assertThat(new TileId(x, y)).hasToString("Tile(%d, %d)", x, y);
    }

}
