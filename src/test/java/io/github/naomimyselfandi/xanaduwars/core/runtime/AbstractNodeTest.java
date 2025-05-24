package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.Player;
import io.github.naomimyselfandi.xanaduwars.core.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.Tile;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import io.github.naomimyselfandi.xanaduwars.core.data.UnitData;
import io.github.naomimyselfandi.xanaduwars.core.queries.NodeDestroyedEvent;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Percent;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TileId;
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
    void hp(SeededRandom random) {
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
        inOrder.verify(gameState).evaluate(new NodeDestroyedEvent(fixture));
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
    void distance(SeededRandom random) {
        var fooId = new TileId(random.nextInt(255), random.nextInt(255));
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
