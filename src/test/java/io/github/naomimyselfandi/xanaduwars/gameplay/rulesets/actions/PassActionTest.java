package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.GameState;
import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class PassActionTest {

    @Mock
    private GameState gameState;

    @Mock
    private Player player;

    private PassAction fixture;

    @BeforeEach
    void setup(SeededRng random) {
        fixture = new PassAction(random.nextName());
    }

    @Test
    void execute() {
        when(player.gameState()).thenReturn(gameState);
        assertThat(fixture.execute(player, None.NONE)).isEqualTo(Execution.SUCCESSFUL);
    }

}
