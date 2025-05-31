package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.GameState;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.EntryQuery;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class MoveLogicImplTest {

    @Mock(strictness = Mock.Strictness.LENIENT)
    private GameState gameState;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private Unit unit, anotherUnit;

    @BeforeEach
    void setup(SeededRng random) {
        when(unit.gameState()).thenReturn(gameState);
        when(unit.speed()).thenReturn(3);
    }

    @Test
    void execute() {
        var tile0 = tile(0, 1.0);
        var tile1 = tile(1, 1.0);
        var tile2 = tile(2, 1.0);
        var path = path(tile0, tile1, tile2);
        assertThat(MoveLogicImpl.MOVE_LOGIC.execute(path, unit)).isEqualTo(Execution.SUCCESSFUL);
        verify(unit, never()).location(tile0);
        var inOrder = inOrder(unit);
        inOrder.verify(unit).location(tile1);
        inOrder.verify(unit).location(tile2);
    }

    @Test
    void execute_WhenANonObstructingUnitIsOnThePath_ThenSkipsThatTile() {
        var tile0 = tile(0, 1.0);
        var tile1 = tile(1, 1.0);
        var tile2 = tile(2, 1.0);
        when(tile1.unit()).thenReturn(Optional.of(anotherUnit));
        var path = path(tile0, tile1, tile2);
        assertThat(MoveLogicImpl.MOVE_LOGIC.execute(path, unit)).isEqualTo(Execution.SUCCESSFUL);
        verify(unit, never()).location(tile0);
        verify(unit, never()).location(tile1);
        verify(unit).location(tile2);
    }

    @Test
    void execute_WhenAUnitIsOnTheLastTile_ThenEntersIt() {
        var tile0 = tile(0, 1.0);
        var tile1 = tile(1, 1.0);
        var tile2 = tile(2, 1.0);
        when(tile2.unit()).thenReturn(Optional.of(anotherUnit));
        var path = path(tile0, tile1, tile2);
        assertThat(MoveLogicImpl.MOVE_LOGIC.execute(path, unit)).isEqualTo(Execution.SUCCESSFUL);
        verify(unit, never()).location(tile0);
        var inOrder = inOrder(unit);
        inOrder.verify(unit).location(tile1);
        inOrder.verify(unit).location(anotherUnit);
        verify(unit, never()).location(tile2);
    }

    @Test
    void execute_WhenTheCostIsTooHigh_ThenTheMovementIsInterrupted() {
        var tile0 = tile(0, 1.0);
        var tile1 = tile(1, 2.0);
        var tile2 = tile(2, 1.5);
        var path = path(tile0, tile1, tile2);
        assertThat(MoveLogicImpl.MOVE_LOGIC.execute(path, unit)).isEqualTo(Execution.INTERRUPTED);
        verify(unit, never()).location(tile0);
        verify(unit).location(tile1);
        verify(unit, never()).location(tile2);
    }

    @Test
    void execute_WhenARuleRejectsTheMovement_ThenTheMovementIsInterrupted() {
        var tile0 = tile(0, 1.0);
        var tile1 = tile(1, 1.0);
        var tile2 = tile(2, Double.NaN);
        var path = path(tile0, tile1, tile2);
        assertThat(MoveLogicImpl.MOVE_LOGIC.execute(path, unit)).isEqualTo(Execution.INTERRUPTED);
        verify(unit, never()).location(tile0);
        verify(unit).location(tile1);
        verify(unit, never()).location(tile2);
    }

    Tile tile(int index, double cost) {
        var tile = mock(Tile.class, withSettings().strictness(Strictness.LENIENT).name("Tile" + index));
        var entryQuery = new EntryQuery(unit, tile);
        when(gameState.evaluate(entryQuery)).thenReturn(cost);
        return tile;
    }

    private static Path path(Tile... tiles) {
        // Prevent exceptions in constructor call.
        for (var i = 1; i < tiles.length; i++) {
            when(tiles[i - 1].distance(tiles[i])).thenReturn(1);
        }
        var start = tiles[0];
        var steps = Arrays.stream(tiles).skip(1).toList();
        return new Path(start, steps);
    }

}
