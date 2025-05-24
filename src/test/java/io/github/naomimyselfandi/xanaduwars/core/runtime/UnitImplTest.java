package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.*;
import io.github.naomimyselfandi.xanaduwars.core.data.UnitData;
import io.github.naomimyselfandi.xanaduwars.core.queries.UnitMovedEvent;
import io.github.naomimyselfandi.xanaduwars.core.queries.UnitStat;
import io.github.naomimyselfandi.xanaduwars.core.queries.UnitStatQuery;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.*;
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
    void setUp(SeededRandom random) {
        lenient().when(gameState.ruleset()).thenReturn(ruleset);
        var unitTypeId = random.nextInt(8);
        var unitTypes = IntStream.range(0, 8).mapToObj(i -> i == unitTypeId ? unitType : mock()).toList();
        lenient().when(ruleset.unitTypes()).thenReturn(unitTypes);
        unitData = new UnitData().unitType(unitTypeId);
        fixture = new UnitImpl(gameState, unitData);
    }

    @Test
    void type() {
        assertThat(fixture.type()).isEqualTo(unitType);
    }

    @Test
    void location_OnTile(SeededRandom random) {
        var tileId = new TileId(random.nextInt(255), random.nextInt(255));
        unitData.location(tileId);
        var tile = mock(Tile.class);
        when(gameState.tile(tileId.x(), tileId.y())).thenReturn(Optional.of(tile));
        assertThat(fixture.location()).isEqualTo(tile);
        assertThat(fixture.tile()).contains(tile);
    }

    @Test
    void location_InUnit(SeededRandom random) {
        var unitId = new UnitId(random.nextInt(255));
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
    void location(String fromKind, String toKind, SeededRandom random) {
        NodeId fromId, nodeId;
        Node from, node;
        if (fromKind.equals("TILE")) {
            var tileId = new TileId(random.nextInt(255), random.nextInt(255));
            var tile = mock(Tile.class);
            when(gameState.tile(tileId.x(), tileId.y())).thenReturn(Optional.of(tile));
            fromId = tileId;
            from = tile;
        } else {
            var unitId = new UnitId(random.nextInt(Integer.MAX_VALUE));
            var unit = mock(Unit.class);
            when(gameState.unit(unitId.intValue())).thenReturn(Optional.of(unit));
            fromId = unitId;
            from = unit;
        }
        if (toKind.equals("TILE")) {
            var tileId = new TileId(random.nextInt(255), random.nextInt(255));
            var tile = mock(Tile.class);
            when(tile.id()).thenReturn(tileId);
            nodeId = tileId;
            node = tile;
        } else {
            var unitId = new UnitId(random.nextInt(Integer.MAX_VALUE));
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
    void location_WhenTheDestinationIsTheOrigin_ThenDoesNothing(String kind, SeededRandom random) {
        Node node;
        if (kind.equals("TILE")) {
            var tileId = new TileId(random.nextInt(255), random.nextInt(255));
            unitData.location(tileId);
            var tile = mock(Tile.class);
            when(gameState.tile(tileId.x(), tileId.y())).thenReturn(Optional.of(tile));
            node = tile;
            var unit = mock(Unit.class);
            lenient().when(tile.unit()).thenReturn(Optional.of(unit));
        } else {
            var unitId = new UnitId(random.nextInt(Integer.MAX_VALUE));
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
    void location_WhenTheDestinationIsAnOccupiedTile_ThenThrows(String fromKind, SeededRandom random) {
        NodeId fromId;
        if (fromKind.equals("TILE")) {
            var tileId = new TileId(random.nextInt(255), random.nextInt(255));
            unitData.location(tileId);
            var tile = mock(Tile.class);
            when(gameState.tile(tileId.x(), tileId.y())).thenReturn(Optional.of(tile));
            fromId = tileId;
        } else {
            var unitId = new UnitId(random.nextInt(Integer.MAX_VALUE));
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
    void cargo(SeededRandom random) {
        var tileId = new TileId(random.nextInt(255), random.nextInt(255));
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
    void vision(SeededRandom random) {
        var base = random.nextInt();
        when(unitType.vision()).thenReturn(base);
        var modified = random.nextInt();
        when(gameState.evaluate(new UnitStatQuery(fixture, UnitStat.VISION), base)).thenReturn(modified);
        assertThat(fixture.vision()).isEqualTo(modified);
    }

    @Test
    void speed(SeededRandom random) {
        var base = random.nextInt();
        when(unitType.speed()).thenReturn(base);
        var modified = random.nextInt();
        when(gameState.evaluate(new UnitStatQuery(fixture, UnitStat.SPEED), base)).thenReturn(modified);
        assertThat(fixture.speed()).isEqualTo(modified);
    }

    @Test
    void range(SeededRandom random) {
        var baseMin = random.nextInt(Integer.MAX_VALUE);
        var baseMax = random.nextInt(Integer.MAX_VALUE);
        var base = new Range(baseMin, baseMax);
        var modifiedMin = random.nextInt(Integer.MAX_VALUE);
        var modifiedMax = random.nextInt(Integer.MAX_VALUE);
        var modified = new Range(modifiedMin, modifiedMax);
        when(unitType.range()).thenReturn(base);
        when(gameState.evaluate(new UnitStatQuery(fixture, UnitStat.MIN_RANGE), baseMin)).thenReturn(modifiedMin);
        when(gameState.evaluate(new UnitStatQuery(fixture, UnitStat.MAX_RANGE), baseMax)).thenReturn(modifiedMax);
        assertThat(fixture.range()).isEqualTo(modified);

    }

    @Test
    void damageTable(SeededRandom random) {
        var foo = mock(UnitType.class);
        var bar = mock(TileType.class);
        var table = Map.of(
                foo, Percent.withDoubleValue(random.nextDouble()),
                bar, Percent.withDoubleValue(random.nextDouble())
        );
        when(unitType.damageTable()).thenReturn(table);
        assertThat(fixture.damageTable()).isEqualTo(table);
    }

    @Test
    void hangar(SeededRandom random) {
        var foo = new Tag("F" + random.nextInt(Integer.MAX_VALUE));
        var bar = new Tag("B" + random.nextInt(Integer.MAX_VALUE));
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
    void onDestruction() {
        fixture.onDestruction();
        verify(gameState).destroyUnit(unitData);
    }

}
