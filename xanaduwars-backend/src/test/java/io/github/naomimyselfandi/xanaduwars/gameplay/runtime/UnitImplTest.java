package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.UnitData;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.UnitMovedEvent;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.UnitStat;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.UnitStatQuery;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class UnitImplTest {

    @Mock
    private AugmentedGameState gameState;

    @Mock
    private Ruleset ruleset;

    @Mock
    private UnitType unitType;
    
    private UnitData unitData;

    private UnitImpl fixture;

    @BeforeEach
    void setUp(SeededRng random) {
        lenient().when(gameState.ruleset()).thenReturn(ruleset);
        var unitTypeId = new UnitTypeId(random.nextInt(8));
        var unitTypes = IntStream.range(0, 8).mapToObj(i -> i == unitTypeId.index() ? unitType : mock()).toList();
        lenient().when(ruleset.unitTypes()).thenReturn(unitTypes);
        unitData = new UnitData().unitType(unitTypeId);
        fixture = new UnitImpl(gameState, unitData);
    }

    @Test
    void type() {
        assertThat(fixture.type()).isEqualTo(unitType);
    }

    @Test
    void location_OnTile(SeededRng random) {
        var tileId = random.nextTileId();
        unitData.location(tileId);
        var tile = mock(Tile.class);
        when(gameState.tile(tileId.x(), tileId.y())).thenReturn(Optional.of(tile));
        assertThat(fixture.location()).isEqualTo(tile);
        assertThat(fixture.tile()).contains(tile);
    }

    @Test
    void location_InUnit(SeededRng random) {
        var unitId = random.nextUnitId();
        unitData.location(unitId);
        var unit = mock(Unit.class);
        when(gameState.unit(unitId.intValue())).thenReturn(Optional.of(unit));
        assertThat(fixture.location()).isEqualTo(unit);
        assertThat(fixture.tile()).isEmpty();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            TILE,TILE
            TILE,UNIT
            UNIT,TILE
            UNIT,UNIT
            """)
    void location(String fromKind, String toKind, SeededRng random) {
        NodeId fromId, nodeId;
        Node from, node;
        if (fromKind.equals("TILE")) {
            var tileId = random.nextTileId();
            var tile = mock(Tile.class);
            when(gameState.tile(tileId.x(), tileId.y())).thenReturn(Optional.of(tile));
            fromId = tileId;
            from = tile;
        } else {
            var unitId = random.nextUnitId();
            var unit = mock(Unit.class);
            when(gameState.unit(unitId.intValue())).thenReturn(Optional.of(unit));
            fromId = unitId;
            from = unit;
        }
        if (toKind.equals("TILE")) {
            var tileId = random.nextTileId();
            var tile = mock(Tile.class);
            when(tile.id()).thenReturn(tileId);
            nodeId = tileId;
            node = tile;
        } else {
            var unitId = random.nextUnitId();
            var unit = mock(Unit.class);
            when(unit.id()).thenReturn(unitId);
            nodeId = unitId;
            node = unit;
        }
        unitData.location(fromId);
        fixture.location(node);
        assertThat(unitData.location()).isEqualTo(nodeId);
        verify(gameState).evaluate(new UnitMovedEvent(fixture, from));
    }

    @ParameterizedTest
    @ValueSource(strings = {"TILE", "UNIT"})
    void location_WhenTheDestinationIsTheOrigin_ThenDoesNothing(String kind, SeededRng random) {
        Node node;
        if (kind.equals("TILE")) {
            var tileId = random.nextTileId();
            unitData.location(tileId);
            var tile = mock(Tile.class);
            when(gameState.tile(tileId.x(), tileId.y())).thenReturn(Optional.of(tile));
            node = tile;
            var unit = mock(Unit.class);
            lenient().when(tile.unit()).thenReturn(Optional.of(unit));
        } else {
            var unitId = random.nextUnitId();
            unitData.location(unitId);
            var unit = mock(Unit.class);
            when(gameState.unit(unitId.intValue())).thenReturn(Optional.of(unit));
            node = unit;
        }
        fixture.location(node);
        verify(gameState, never()).evaluate(any(UnitMovedEvent.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"TILE", "UNIT"})
    void location_WhenTheDestinationIsAnOccupiedTile_ThenThrows(String fromKind, SeededRng random) {
        NodeId fromId;
        if (fromKind.equals("TILE")) {
            var tileId = random.nextTileId();
            unitData.location(tileId);
            var tile = mock(Tile.class);
            when(gameState.tile(tileId.x(), tileId.y())).thenReturn(Optional.of(tile));
            fromId = tileId;
        } else {
            var unitId = random.nextUnitId();
            unitData.location(unitId);
            var unit = mock(Unit.class);
            when(gameState.unit(unitId.intValue())).thenReturn(Optional.of(unit));
            fromId = unitId;
        }
        var tile = mock(Tile.class);
        var unit = mock(Unit.class);
        when(tile.unit()).thenReturn(Optional.of(unit));
        assertThatThrownBy(() -> fixture.location(tile)).isInstanceOf(IllegalStateException.class);
        assertThat(unitData.location()).isEqualTo(fromId);
        verify(gameState, never()).evaluate(any(UnitMovedEvent.class));
    }

    @Test
    void cargo(SeededRng random) {
        var tileId = random.nextTileId();
        unitData.location(tileId);
        var tile = mock(Tile.class);
        when(gameState.tile(tileId.x(), tileId.y())).thenReturn(Optional.of(tile));
        var foo = mock(Unit.class);
        when(foo.location()).thenReturn(fixture);
        var bar = mock(Unit.class);
        when(bar.location()).thenReturn(fixture);
        tile = mock(Tile.class);
        var baz = mock(Unit.class);
        when(baz.location()).thenReturn(tile);
        var bat = mock(Unit.class);
        when(bat.location()).thenReturn(baz);
        when(gameState.units()).thenReturn(List.of(foo, baz, fixture, bar, bat));
        assertThat(fixture.cargo()).containsExactly(foo, bar);
    }

    @Test
    void canAct() {
        assertThat(fixture.canAct()).isFalse();
        fixture.canAct(true);
        assertThat(fixture.canAct()).isTrue();
        assertThat(unitData.canAct()).isTrue();
        fixture.canAct(false);
        assertThat(fixture.canAct()).isFalse();
        assertThat(unitData.canAct()).isFalse();
    }

    @Test
    void vision(SeededRng random) {
        var expected = random.nextInt();
        when(gameState.evaluate(new UnitStatQuery(fixture, UnitStat.VISION))).thenReturn(expected);
        assertThat(fixture.vision()).isEqualTo(expected);
    }

    @Test
    void speed(SeededRng random) {
        var expected = random.nextInt();
        when(gameState.evaluate(new UnitStatQuery(fixture, UnitStat.SPEED))).thenReturn(expected);
        assertThat(fixture.speed()).isEqualTo(expected);
    }

    @Test
    void range(SeededRng random) {
        var min = random.nextIntNotNegative();
        var max = random.nextIntNotNegative();
        var expected = new Range(min, max);
        when(gameState.evaluate(new UnitStatQuery(fixture, UnitStat.MIN_RANGE))).thenReturn(min);
        when(gameState.evaluate(new UnitStatQuery(fixture, UnitStat.MAX_RANGE))).thenReturn(max);
        assertThat(fixture.range()).isEqualTo(expected);

    }

    @Test
    void damageTable(SeededRng random) {
        var foo = mock(UnitType.class);
        var bar = mock(TileType.class);
        var table = Map.of(
                foo, random.nextScalar(),
                bar, random.nextPercent()
        );
        when(unitType.damageTable()).thenReturn(table);
        assertThat(fixture.damageTable()).isEqualTo(table);
    }

    @Test
    void hangar(SeededRng random) {
        var foo = random.nextTag();
        var bar = random.nextTag();
        when(unitType.hangar()).thenReturn(TagSet.of(foo, bar));
        assertThatCollection(fixture.hangar()).containsOnly(foo, bar);
    }

    @Test
    void abilities() {
        Action<Unit, Object> foo = mock();
        Action<Unit, Object> bar = mock();
        when(unitType.abilities()).thenReturn(List.of(foo, bar));
        assertThat(fixture.abilities()).containsExactly(foo, bar);
    }

    @Test
    void onDestruction(SeededRng random) {
        var tileId = random.nextTileId();
        unitData.location(tileId);
        var tile = mock(Tile.class);
        when(gameState.tile(tileId.x(), tileId.y())).thenReturn(Optional.of(tile));
        var foo = mock(Unit.class);
        when(foo.location()).thenReturn(fixture);
        var bar = mock(Unit.class);
        when(bar.location()).thenReturn(fixture);
        when(gameState.units()).thenReturn(List.of(foo, fixture, bar));
        fixture.onDestruction();
        var inOrder = inOrder(foo, bar, gameState);
        inOrder.verify(foo).hp(Percent.ZERO);
        inOrder.verify(bar).hp(Percent.ZERO);
        inOrder.verify(gameState).destroyUnit(unitData);
    }

}
