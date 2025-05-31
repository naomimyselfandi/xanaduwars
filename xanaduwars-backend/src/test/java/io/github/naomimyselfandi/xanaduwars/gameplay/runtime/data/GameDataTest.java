package io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameDataTest {

    @Test
    void testHashCode() {
        assertThat(new GameData()).hasSameHashCodeAs(GameData.class);
    }

}
