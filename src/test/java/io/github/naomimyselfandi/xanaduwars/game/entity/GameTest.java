package io.github.naomimyselfandi.xanaduwars.game.entity;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.TreeMap;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class GameTest {

    private Game fixture;

    @BeforeEach
    void setup() {
        fixture = new Game();
    }

    @EnumSource
    @ParameterizedTest
    void hasStarted(Game.Status status) {
        assertThat(fixture.setStatus(status)).isSameAs(fixture);
        assertThat(fixture.hasStarted()).isEqualTo(switch (status) {
            case PENDING, CANCELED -> false;
            case ONGOING, FINISHED -> true;
        });
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void hasUniquePlayers(boolean value, SeededRng random) {
        var slot0 = random.<PlayerSlot>get();
        var slot1 = random.<PlayerSlot>get();
        if (!value) slot1.setAccount(slot0.getAccount());
        var slots = new TreeMap<PlayerId, PlayerSlot>();
        slots.put(random.get(), slot0);
        slots.put(random.get(), slot1);
        assertThat(fixture.setPlayerSlots(slots)).isSameAs(fixture);
        assertThat(fixture.hasUniquePlayers()).isEqualTo(value);
    }

}
