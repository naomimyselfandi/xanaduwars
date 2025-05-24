package io.github.naomimyselfandi.xanaduwars.core.actions;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.Execution;
import io.github.naomimyselfandi.xanaduwars.core.GameState;
import io.github.naomimyselfandi.xanaduwars.core.Player;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
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
    void setup(SeededRandom random) {
        fixture = new PassAction(new Name("Z" + random.nextInt(Integer.MAX_VALUE)), TagSet.EMPTY);
    }

    @Test
    void execute() {
        when(player.gameState()).thenReturn(gameState);
        assertThat(fixture.execute(player, None.NONE)).isEqualTo(Execution.SUCCESSFUL);
    }

}
