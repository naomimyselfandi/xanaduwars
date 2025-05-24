package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.*;
import io.github.naomimyselfandi.xanaduwars.core.data.GameData;
import io.github.naomimyselfandi.xanaduwars.core.data.PlayerData;
import io.github.naomimyselfandi.xanaduwars.core.data.TileData;
import io.github.naomimyselfandi.xanaduwars.core.data.UnitData;
import io.github.naomimyselfandi.xanaduwars.core.QueryEvaluator;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.*;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({SeededRandomExtension.class, MockitoExtension.class})
class GameStateImplTest {

    @Mock
    private Action<Unit, ?> action;

    @Mock
    private Unit unit;

    @Mock
    private UnitType unitType0, unitType1;

    @Mock
    private Commander commander;

    @Mock
    private Ruleset ruleset;

    @Mock
    private QueryEvaluator queryEvaluator;

    @Mock
    private ActionPolicy actionPolicy;

    @Mock
    private ActionExecutor actionExecutor;

    private GameData gameData;

    private PlayerData playerData0, playerData1, playerData2;

    private TileData tileData0, tileData1, tileData2, tileData3, tileData4, tileData5;

    private GameStateImpl fixture;

    @BeforeEach
    void setup(SeededRandom random) {
        lenient().when(unitType0.index()).thenReturn(0);
        lenient().when(unitType1.index()).thenReturn(1);
        when(ruleset.commanders()).thenReturn(List.of(commander));
        var versionNumber = new VersionNumber("%d.%d.%d".formatted(
                random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255)
        ));
        playerData0 = new PlayerData().playerId(new PlayerId(0)).playerType(0);
        playerData1 = new PlayerData().playerId(new PlayerId(1)).playerType(0);
        playerData2 = new PlayerData().playerId(new PlayerId(2)).playerType(0);
        tileData0 = new TileData().tileId(new TileId(0, 0));
        tileData1 = new TileData().tileId(new TileId(1, 0));
        tileData2 = new TileData().tileId(new TileId(2, 0));
        tileData3 = new TileData().tileId(new TileId(0, 1));
        tileData4 = new TileData().tileId(new TileId(1, 1));
        tileData5 = new TileData().tileId(new TileId(2, 1));
        gameData = new GameData().versionNumber(versionNumber);
        gameData.players().addAll(List.of(playerData0, playerData1, playerData2));
        gameData.tiles().addAll(List.of(tileData0, tileData1, tileData2, tileData3, tileData4, tileData5));
        gameData.activePlayer(new PlayerId(0)).width(3);
        fixture = new GameStateImpl(gameData, ruleset, mock(), queryEvaluator, actionPolicy, actionExecutor);
    }

    @Test
    void actions() {
        when(actionPolicy.actions(ruleset, unit)).thenReturn(List.of(action));
        assertThat(fixture.actions(unit)).containsExactly(action);
    }

    @Test
    void tiles() {
        assertThat(fixture.tiles()).containsExactly(
                List.of(new TileImpl(fixture, tileData0),
                        new TileImpl(fixture, tileData1),
                        new TileImpl(fixture, tileData2)),
                List.of(new TileImpl(fixture, tileData3),
                        new TileImpl(fixture, tileData4),
                        new TileImpl(fixture, tileData5))
        );
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0,0
            1,0,1
            0,1,3
            2,1,5
            -1,0,
            3,0,
            0,-1,
            0,2,
            """)
    void tile(int x, int y, @Nullable Integer index) {
        if (index == null) {
            assertThat(fixture.tile(x, y)).isEmpty();
        } else {
            assertThat(fixture.tile(x, y)).contains(new TileImpl(fixture, gameData.tiles().get(index)));
        }
    }

    @Test
    void units() {
        var unit0 = fixture.createUnit(fixture.tiles().getFirst().getFirst(), unitType0);
        var unitData0 = gameData.units().lastEntry().getValue();
        assertThat(unit0).isEqualTo(new UnitImpl(fixture, unitData0));
        for (var field : UnitData.Fields.values()) {
            var _ = switch (field) {
                case unitId -> assertThat(unitData0.unitId()).isEqualTo(new UnitId(0));
                case unitType -> assertThat(unitData0.unitType()).isZero();
                case owner -> assertThat(unitData0.owner()).isEqualTo(null);
                case hitpoints -> assertThat(unitData0.hitpoints()).isEqualTo(Percent.FULL);
                case location -> assertThat(unitData0.location()).isEqualTo(new TileId(0, 0));
                case canAct -> assertThat(unitData0.canAct()).isFalse();
            };
        }
        assertThat(fixture.unit(0)).contains(unit0);
        var unit1 = fixture.createUnit(fixture.tiles().getLast().getLast(), unitType1);
        var unitData1 = gameData.units().lastEntry().getValue();
        assertThat(unit1).isEqualTo(new UnitImpl(fixture, unitData1));
        for (var field : UnitData.Fields.values()) {
            var _ = switch (field) {
                case unitId -> assertThat(unitData1.unitId()).isEqualTo(new UnitId(1));
                case unitType -> assertThat(unitData1.unitType()).isOne();
                case owner -> assertThat(unitData1.owner()).isEqualTo(null);
                case hitpoints -> assertThat(unitData1.hitpoints()).isEqualTo(Percent.FULL);
                case location -> assertThat(unitData1.location()).isEqualTo(new TileId(2, 1));
                case canAct -> assertThat(unitData1.canAct()).isFalse();
            };
        }
        assertThat(fixture.unit(1)).contains(unit1);
        assertThat(gameData.nextUnitId()).isEqualTo(2);
        fixture.destroyUnit(unitData1);
        assertThat(fixture.units()).containsExactly(unit0);
        assertThat(fixture.unit(1)).isEmpty();
        var unit2 = fixture.createUnit(fixture.tiles().getLast().getLast(), unitType1);
        assertThat(fixture.unit(2)).contains(unit2);
    }

    @Test
    void players() {
        assertThat(fixture.players()).containsExactly(
                new PlayerImpl(fixture, playerData0),
                new PlayerImpl(fixture, playerData1),
                new PlayerImpl(fixture, playerData2)
        );
    }

    @Test
    void pass() {
        var player0 = fixture.players().get(0);
        var player1 = fixture.players().get(1);
        var player2 = fixture.players().get(2);
        assertThat(fixture.activePlayer()).isEqualTo(player0);
        assertThat(fixture.turn()).isZero();
        fixture.pass();
        assertThat(fixture.activePlayer()).isEqualTo(player1);
        assertThat(fixture.turn()).isZero();
        fixture.pass();
        assertThat(fixture.activePlayer()).isEqualTo(player2);
        assertThat(fixture.turn()).isZero();
        fixture.pass();
        assertThat(fixture.turn()).isOne();
        assertThat(fixture.activePlayer()).isEqualTo(player0);
        fixture.pass();
        assertThat(fixture.turn()).isOne();
        assertThat(fixture.activePlayer()).isEqualTo(player1);
        fixture.pass();
        assertThat(fixture.turn()).isOne();
        assertThat(fixture.activePlayer()).isEqualTo(player2);
        fixture.pass();
        assertThat(fixture.turn()).isEqualTo(2);
        assertThat(fixture.activePlayer()).isEqualTo(player0);
    }

    @Test
    void pass_SkipsDefeatedPlayers() {
        var player0 = fixture.players().get(0);
        var player1 = fixture.players().get(1);
        var player2 = fixture.players().get(2);
        assertThat(fixture.activePlayer()).isEqualTo(player0);
        assertThat(fixture.turn()).isZero();
        fixture.pass();
        assertThat(fixture.activePlayer()).isEqualTo(player1);
        assertThat(fixture.turn()).isZero();
        player1.defeat();
        fixture.pass();
        assertThat(fixture.activePlayer()).isEqualTo(player2);
        assertThat(fixture.turn()).isZero();
        fixture.pass();
        assertThat(fixture.turn()).isOne();
        assertThat(fixture.activePlayer()).isEqualTo(player0);
        fixture.pass();
        assertThat(fixture.turn()).isOne();
        assertThat(fixture.activePlayer()).isEqualTo(player2);
        fixture.pass();
        assertThat(fixture.turn()).isEqualTo(2);
        assertThat(fixture.activePlayer()).isEqualTo(player0);
        fixture.pass();
        player0.defeat();
        fixture.pass();
        assertThat(fixture.turn()).isEqualTo(3);
        fixture.pass();
        assertThat(fixture.turn()).isEqualTo(4);
        player2.defeat();
        fixture.pass();
        assertThat(fixture.turn()).isEqualTo(5);
    }

}
