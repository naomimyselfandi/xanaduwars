package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.TileTypeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.TurnStartEvent;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.*;
import io.github.naomimyselfandi.xanaduwars.core.scripting.*;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameStateImplTest {

    private StructureData oldStructureData;

    @Mock
    private EventObserver observer;

    @Mock
    private Player player;

    @Mock
    private StructureType structureType;

    private UnitData oldUnitData;

    @Mock
    private UnitType unitType;

    @Mock
    private Ruleset ruleset;

    @Mock
    private CopyMachine copyMachine;

    @Mock
    private Redactor redactor;

    private GameStateData gameStateData;

    @Mock
    private QueryEvaluator queryEvaluator;

    @Mock
    private Commander commander;

    @Mock
    private Spell abracadabra, alakazam;

    @Mock
    private SpellSlotHelper spellSlotHelper;

    private GameStateImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        when(structureType.getId()).thenReturn(random.get());
        when(unitType.getId()).thenReturn(random.get());
        gameStateData = new GameStateData()
                .setPlayers(List.of(
                        new PlayerData().setTeam(new Team(0)),
                        new PlayerData().setTeam(new Team(1)),
                        new PlayerData().setTeam(new Team(0)),
                        new PlayerData().setTeam(new Team(1))
                ))
                .setTiles(new TreeMap<>(Map.of(
                        new TileId(0, 0), new TileData().setTypeId(new TileTypeId(0)),
                        new TileId(1, 0), new TileData().setTypeId(new TileTypeId(0)),
                        new TileId(0, 1), new TileData().setTypeId(new TileTypeId(0)),
                        new TileId(1, 1), new TileData().setTypeId(new TileTypeId(0))
                )));
        oldStructureData = gameStateData.createStructure(new TileId(0, 0), random.get());
        oldUnitData = gameStateData.createUnit(new TileId(1, 1), random.get()).getValue();
        fixture = new GameStateImpl(ruleset, gameStateData, queryEvaluator, copyMachine, redactor, spellSlotHelper);
    }

    @Test
    void evaluate(SeededRng random) {
        var value = random.nextInt();
        var query = random.<FooQuery>get();
        when(queryEvaluator.evaluate(fixture, query)).thenReturn(value);
        assertThat(fixture.evaluate(query)).isEqualTo(value);
    }

    @Test
    void evaluate_CachesQueries(SeededRng random) {
        var value0 = random.nextInt();
        var value1 = random.nextInt();
        var query = random.<FooQuery>get();
        when(queryEvaluator.evaluate(fixture, query)).thenReturn(value0).thenReturn(value1);
        assertThat(fixture.evaluate(query)).isEqualTo(value0);
        assertThat(fixture.evaluate(query)).isEqualTo(value0);
    }

    @Test
    void evaluate_WhenAQueryDependsOnItself_ThenThrows(SeededRng random) {
        var query = random.<FooQuery>get();
        doAnswer(_ -> fixture.evaluate(query)).when(queryEvaluator).evaluate(fixture, query);
        assertThatThrownBy(() -> fixture.evaluate(query))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("%s depends on itself!", query);
    }

    @Test
    void evaluate_WhenAnEventIsEvaluated_ThenInvalidatesTheCache(SeededRng random) {
        var value0 = random.nextInt();
        var value1 = random.nextInt();
        var query = random.<FooQuery>get();
        when(queryEvaluator.evaluate(fixture, query)).thenReturn(value0).thenReturn(value1);
        assertThat(fixture.evaluate(query)).isEqualTo(value0);
        fixture.evaluate(random.<BarEvent>get());
        assertThat(fixture.evaluate(query)).isEqualTo(value1);
    }

    @Test
    void evaluate_WhenAnEventIsEvaluated_ThenNotifiesObservers(SeededRng random) {
        var event = random.<BarEvent>get();
        assertThat(fixture.attachObserver(observer)).isSameAs(fixture);
        fixture.evaluate(event);
        var inOrder = inOrder(queryEvaluator, observer);
        inOrder.verify(queryEvaluator).evaluate(fixture, event);
        inOrder.verify(observer).accept(event);
    }

    @Test
    void invalidateCache(SeededRng random) {
        var value0 = random.nextInt();
        var value1 = random.nextInt();
        var query = random.<FooQuery>get();
        when(queryEvaluator.evaluate(fixture, query)).thenReturn(value0).thenReturn(value1);
        assertThat(fixture.evaluate(query)).isEqualTo(value0);
        fixture.invalidateCache();
        assertThat(fixture.evaluate(query)).isEqualTo(value1);
    }

    @Test
    void limitedTo(SeededRng random) {
        var copyState = random.<GameStateData>get();
        when(copyMachine.copy(gameStateData)).thenReturn(copyState);
        assertThat(fixture.limitedTo(player)).isInstanceOfSatisfying(GameStateImpl.class, copy -> {
            assertThat(copy.getRuleset()).isEqualTo(ruleset);
            assertThat(copy.gameStateData).isEqualTo(copyState);
            assertThat(copy.queryEvaluator).isEqualTo(queryEvaluator);
            assertThat(copy.copyMachine).isEqualTo(copyMachine);
            assertThat(copy.redactor).isEqualTo(redactor);
            assertThat(copy.isLimitedCopy()).isTrue();
        });
    }

    @Test
    void limitedTo_WhenTheOriginalGameHasObservers_ThenTheCopyDoesNotInheritThem(SeededRng random) {
        fixture.attachObserver(observer);
        var copyState = random.<GameStateData>get();
        when(copyMachine.copy(gameStateData)).thenReturn(copyState);
        var event = random.<BarEvent>get();
        fixture.limitedTo(player).evaluate(event);
        verify(observer, never()).accept(event);
    }

    @Test
    void isLimitedCopy() {
        assertThat(fixture.isLimitedCopy()).isFalse();
    }

    @Test
    void units(SeededRng random) {
        var tiles = fixture.getTiles();
        var oldUnitTile = tiles.get(new TileId(1, 1));
        var newUnitTile = tiles.get(new TileId(0, 1));
        var oldUnit = new UnitImpl(oldUnitData, fixture, new UnitId(0));
        var newUnit = fixture.createUnit(newUnitTile, unitType);
        assertThat(fixture.getUnits())
                .hasSize(2)
                .containsEntry(new UnitId(0), oldUnit)
                .containsEntry(new UnitId(1), newUnit);
        assertThat(oldUnitTile.getUnit()).isEqualTo(oldUnit);
        assertThat(oldUnit.getTile()).isEqualTo(oldUnitTile);
        assertThat(newUnitTile.getUnit()).isEqualTo(newUnit);
        assertThat(newUnit.getTile()).isEqualTo(newUnitTile);
        var destination = tiles.get(new TileId(1, 0));
        fixture.moveUnit(newUnit, destination);
        assertThat(oldUnitTile.getUnit()).isEqualTo(oldUnit);
        assertThat(oldUnit.getTile()).isEqualTo(oldUnitTile);
        assertThat(destination.getUnit()).isEqualTo(newUnit);
        assertThat(newUnit.getTile()).isEqualTo(destination);
        assertThat(newUnitTile.getUnit()).isNull();
        oldUnit.setHp(Hp.ZERO);
        newUnit.setHp(random.not(Hp.ZERO));
        assertThat(fixture.getUnits()).hasSize(1).containsEntry(new UnitId(1), newUnit);
        assertThat(oldUnitTile.getUnit()).isNull();
        assertThat(destination.getUnit()).isEqualTo(newUnit);
        assertThat(newUnit.getTile()).isEqualTo(destination);
        assertThat(newUnitTile.getUnit()).isNull();
    }

    @Test
    void structures(SeededRng random) {
        var tiles = fixture.getTiles();
        var oldStructureTile = tiles.get(new TileId(0, 0));
        var newStructureTile = tiles.get(new TileId(1, 0));
        var oldStructure = new StructureImpl(oldStructureData, fixture, new StructureId(0, 0));
        var newStructure = fixture.createStructure(newStructureTile, structureType);
        assertThat(fixture.getStructures())
                .hasSize(2)
                .containsEntry(new StructureId(0, 0), oldStructure)
                .containsEntry(new StructureId(1, 0), newStructure);
        assertThat(oldStructureTile.getStructure()).isEqualTo(oldStructure);
        assertThat(oldStructure.getTile()).isEqualTo(oldStructureTile);
        assertThat(newStructureTile.getStructure()).isEqualTo(newStructure);
        assertThat(newStructure.getTile()).isEqualTo(newStructureTile);
        oldStructure.setHp(Hp.ZERO);
        newStructure.setHp(random.not(Hp.ZERO));
        assertThat(fixture.getStructures()).hasSize(1).containsEntry(new StructureId(1, 0), newStructure);
        assertThat(oldStructureTile.getStructure()).isNull();
        assertThat(newStructureTile.getStructure()).isEqualTo(newStructure);
        assertThat(newStructure.getTile()).isEqualTo(newStructureTile);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0
            1,1
            2,2
            3,3
            4,0
            5,1
            6,2
            7,3
            """)
    void turns(int turnIndex, int activePlayerIndex) {
        var player = fixture.getPlayers().get(activePlayerIndex);
        var turn = new Turn(turnIndex);
        doAnswer(_ -> {
            assertThat(fixture.getTurn()).isEqualTo(turn);
            return None.NONE;
        }).when(queryEvaluator).evaluate(fixture, new TurnStartEvent(player));
        assertThat(fixture.setTurn(turn)).isSameAs(fixture);
        verify(queryEvaluator).evaluate(fixture, new TurnStartEvent(player));
        assertThat(fixture.getTurn()).isEqualTo(turn);
        assertThat(gameStateData.getTurn()).isEqualTo(turn);
        assertThat(fixture.getActivePlayer()).isEqualTo(player);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void pass(boolean passed) {
        assertThat(fixture.setPassed(passed)).isSameAs(fixture);
        assertThat(fixture.isPassed()).isEqualTo(passed);
        assertThat(gameStateData.isPassed()).isEqualTo(passed);
    }

}
