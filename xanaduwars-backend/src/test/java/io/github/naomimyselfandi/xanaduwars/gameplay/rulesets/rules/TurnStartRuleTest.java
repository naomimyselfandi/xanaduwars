package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.GameState;
import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TurnStartEvent;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TurnStartEventForTile;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TurnStartEventForUnit;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TurnStartRuleTest {

    @Mock
    private GameState gameState;

    @Mock
    private Player player;

    @Mock
    private Tile tile;

    @Mock
    private Unit unit;

    @InjectMocks
    private TurnStartEvent event;

    @InjectMocks
    private TurnStartRule fixture;

    @Test
    void handles() {
        assertThat(fixture.handles(event, None.NONE)).isTrue();
    }

    @Test
    void handle() {
        when(player.gameState()).thenReturn(gameState);
        when(player.tiles()).then(_ -> Stream.of(tile));
        when(player.units()).then(_ -> Stream.of(unit));
        assertThat(fixture.handle(event, None.NONE)).isEqualTo(None.NONE);
        var inOrder = inOrder(player, gameState);
        inOrder.verify(player).clearActiveSpells();
        inOrder.verify(gameState).evaluate(new TurnStartEventForTile(tile));
        inOrder.verify(gameState).evaluate(new TurnStartEventForUnit(unit));
    }

}
