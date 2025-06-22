package io.github.naomimyselfandi.xanaduwars.core.gamestate;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class TurnTest {

    @Test
    void testToString(SeededRng random) {
        var turn = random.<Turn>get();
        assertThat(turn).hasToString(String.valueOf(turn.ordinal()));
    }

    @Test
    void json(SeededRng random) {
        var turn = random.<Turn>get();
        TestUtils.assertJson(turn, String.valueOf(turn.ordinal()));
    }

}
