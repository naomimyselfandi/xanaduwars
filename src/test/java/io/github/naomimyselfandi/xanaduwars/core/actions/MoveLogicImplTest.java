package io.github.naomimyselfandi.xanaduwars.core.actions;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.Execution;
import io.github.naomimyselfandi.xanaduwars.core.GameState;
import io.github.naomimyselfandi.xanaduwars.core.Tile;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import io.github.naomimyselfandi.xanaduwars.core.queries.EntryQuery;
import io.github.naomimyselfandi.xanaduwars.core.queries.Path;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Tag;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class MoveLogicImplTest {

    @Mock(strictness = Mock.Strictness.LENIENT)
    private GameState gameState;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private Unit unit, anotherUnit;

    private Tag foo, bar;

    @BeforeEach
    void setup(SeededRandom random) {
        when(unit.gameState()).thenReturn(gameState);
        foo = new Tag("F" + random.nextInt(255));
        bar = new Tag("B" + random.nextInt(255));
        when(unit.tags()).thenReturn(TagSet.of(foo, bar));
        when(unit.speed()).thenReturn(3);
    }

    @Test
    void execute() {
        var tile0 = tile(0, foo, 1.0, 1.0);
        var tile1 = tile(1, foo, 1.0, 1.0);
        var tile2 = tile(2, foo, 1.0, 1.0);
        var path = path(tile0, tile1, tile2);
        assertThat(MoveLogicImpl.MOVE_LOGIC.execute(path, unit)).isEqualTo(Execution.SUCCESSFUL);
        verify(unit, never()).location(tile0);
        var inOrder = inOrder(unit);
        inOrder.verify(unit).location(tile1);
        inOrder.verify(unit).location(tile2);
    }

    @Test
    void execute_UsesTheLowestSpeedCost() {
        var tile0 = tile(0, foo, Map.of(foo, 1.0, bar, 2.0), 1.0);
        var tile1 = tile(1, foo, Map.of(foo, 1.0, bar, 2.0), 1.0);
        var tile2 = tile(2, foo, Map.of(foo, 1.0, bar, 2.0), 1.0);
        var path = path(tile0, tile1, tile2);
        assertThat(MoveLogicImpl.MOVE_LOGIC.execute(path, unit)).isEqualTo(Execution.SUCCESSFUL);
        verify(unit, never()).location(tile0);
        var inOrder = inOrder(unit);
        inOrder.verify(unit).location(tile1);
        inOrder.verify(unit).location(tile2);
    }

    @Test
    void execute_WhenANonObstructingUnitIsOnThePath_ThenSkipsThatTile() {
        var tile0 = tile(0, foo, 1.0, 1.0);
        var tile1 = tile(1, foo, 1.0, 1.0);
        var tile2 = tile(2, foo, 1.0, 1.0);
        when(tile1.unit()).thenReturn(Optional.of(anotherUnit));
        var path = path(tile0, tile1, tile2);
        assertThat(MoveLogicImpl.MOVE_LOGIC.execute(path, unit)).isEqualTo(Execution.SUCCESSFUL);
        verify(unit, never()).location(tile0);
        verify(unit, never()).location(tile1);
        verify(unit).location(tile2);
    }

    @Test
    void execute_WhenAUnitIsOnTheLastTile_ThenEntersIt() {
        var tile0 = tile(0, foo, 1.0, 1.0);
        var tile1 = tile(1, foo, 1.0, 1.0);
        var tile2 = tile(2, foo, 1.0, 1.0);
        when(tile2.unit()).thenReturn(Optional.of(anotherUnit));
        var path = path(tile0, tile1, tile2);
        assertThat(MoveLogicImpl.MOVE_LOGIC.execute(path, unit)).isEqualTo(Execution.SUCCESSFUL);
        verify(unit, never()).location(tile0);
        var inOrder = inOrder(unit);
        inOrder.verify(unit).location(tile1);
        inOrder.verify(unit).location(anotherUnit);
        verify(unit, never()).location(tile2);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void execute_WhenTheCostIsTooHigh_ThenTheMovementIsInterrupted(boolean modified) {
        var tile0 = tile(0, foo, 1.0, 1.0);
        var tile1 = tile(1, foo, modified ? 1.0 : 2.0, 2.0);
        var tile2 = tile(2, foo, modified ? 1.0 : 1.5, 1.5);
        var path = path(tile0, tile1, tile2);
        assertThat(MoveLogicImpl.MOVE_LOGIC.execute(path, unit)).isEqualTo(Execution.INTERRUPTED);
        verify(unit, never()).location(tile0);
        verify(unit).location(tile1);
        verify(unit, never()).location(tile2);
    }

    @Test
    void execute_WhenARuleRejectsTheMovement_ThenTheMovementIsInterrupted() {
        var tile0 = tile(0, foo, 1.0, 1.0);
        var tile1 = tile(1, foo, 1.0, 1.0);
        var tile2 = tile(2, foo, 1.0, Double.NaN);
        var path = path(tile0, tile1, tile2);
        assertThat(MoveLogicImpl.MOVE_LOGIC.execute(path, unit)).isEqualTo(Execution.INTERRUPTED);
        verify(unit, never()).location(tile0);
        verify(unit).location(tile1);
        verify(unit, never()).location(tile2);
    }

    Tile tile(int index, Tag tag, double baseCost, double cost) {
        return tile(index, tag, Map.of(tag, baseCost), cost);
    }

    Tile tile(int index, Tag tag, Map<Tag, Double> movementTable, double cost) {
        var tile = mock(Tile.class, withSettings().strictness(Strictness.LENIENT).name("Tile" + index)) ;
        var baseCost = movementTable.getOrDefault(tag, Double.NaN);
        var entryQuery = new EntryQuery(unit, tile);
        when(tile.movementTable()).thenReturn(movementTable);
        when(gameState.evaluate(entryQuery, baseCost)).thenReturn(cost);
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
