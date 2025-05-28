package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.Ruleset;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.UnitData;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.UnitDestroyedEvent;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Percent;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.PlayerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AbstractNodeTest {

    @Mock
    private AugmentedGameState gameState;

    @Mock
    private Ruleset ruleset;

    private UnitData unitData;

    private UnitImpl fixture;

    @BeforeEach
    void setup() {
        lenient().when(gameState.ruleset()).thenReturn(ruleset);
        unitData = new UnitData();
        fixture = new UnitImpl(gameState, unitData);
    }

    @Test
    void hp(SeededRng random) {
        var percent = new Percent(random.nextInt(1, 100) / 100.0);
        assertThat(fixture.hp()).isEqualTo(Percent.FULL);
        fixture.hp(percent);
        assertThat(fixture.hp()).isEqualTo(percent);
        assertThat(unitData.hitpoints()).isEqualTo(percent);
    }

    @Test
    void hp_Zero() {
        fixture.hp(Percent.ZERO);
        var inOrder = inOrder(gameState);
        inOrder.verify(gameState).evaluate(new UnitDestroyedEvent(fixture));
        inOrder.verify(gameState).destroyUnit(unitData);
    }

    @Test
    void owner() {
        var foo = mock(Player.class);
        unitData.owner(new PlayerId(0));
        var bar = mock(Player.class);
        when(bar.id()).thenReturn(new PlayerId(1));
        when(gameState.players()).thenReturn(List.of(foo, bar));
        assertThat(fixture.owner()).contains(foo);
        fixture.owner(bar);
        assertThat(fixture.owner()).contains(bar);
        assertThat(unitData.owner()).isEqualTo(new PlayerId(1));
        fixture.owner(null);
        assertThat(fixture.owner()).isEmpty();
    }

    @Test
    void distance(SeededRng random) {
        var fooId = random.nextTileId();
        unitData.location(fooId);
        var foo = mock(Tile.class);
        when(gameState.tile(fooId.x(), fooId.y())).thenReturn(Optional.of(foo));
        var bar = mock(Tile.class);
        var target = mock(Unit.class);
        when(target.tile()).thenReturn(Optional.of(bar));
        var distance = random.nextInt(255);
        when(foo.distance(bar)).thenReturn(distance);
        assertThat(fixture.distance(target)).contains(distance);
    }

}
