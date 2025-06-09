package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class GameStateIdTest {

    @Test
    void testToString(SeededRng random) {
        var gameId = random.<GameStateId>get();
        assertThat(gameId).hasToString(gameId.id().toString());
    }

}
