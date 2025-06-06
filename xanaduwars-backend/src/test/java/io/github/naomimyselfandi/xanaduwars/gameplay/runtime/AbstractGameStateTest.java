package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.GameData;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.GameDataFactory;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.PlayerData;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.TileData;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.QueryEvaluator;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.VisionQuery;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AbstractGameStateTest {

    @Mock
    private Action<Unit, Object> action;

    @Mock
    private Unit unit;

    @Mock
    private Commander commander;

    @Mock
    private Ruleset ruleset;

    @Mock
    private QueryEvaluator queryEvaluator;

    @Mock
    private GameDataFactory gameDataFactory;

    @Mock
    private ActionPolicy actionPolicy;

    @Mock
    private ActionExecutor actionExecutor;

    private Version version;

    private GameData gameData;

    private AugmentedGameState fixture;

    @BeforeEach
    void setup(SeededRng random) {
        when(ruleset.commanders()).thenReturn(List.of(commander));
        version = random.nextVersion();
        gameData = gameData(version);
        fixture = new GameStateImpl(gameData, ruleset, gameDataFactory, queryEvaluator, actionPolicy, actionExecutor);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void execute(boolean problem) throws ActionException {
        if (problem) doThrow(new ActionException(null, null)).when(actionExecutor).execute(any(), any());
        var item = new ActionItem<>(action, new Object());
        var tile1x0 = fixture.tile(1, 0).orElseThrow();
        var tile0x1 = fixture.tile(0, 1).orElseThrow();
        var tile1x1 = fixture.tile(1, 1).orElseThrow();
        when(queryEvaluator.evaluate(any(VisionQuery.class))).then(invocation -> {
            var query = invocation.<VisionQuery>getArgument(0);
            if (query.target().equals(tile1x1)) {
                return true;
            } else if (query.target().equals(tile1x0)) {
                return query.subject().id().intValue() == 0;
            } else if (query.target().equals(tile0x1)) {
                return query.subject().id().intValue() == 1;
            } else {
                return false;
            }
        });
        var data0x0 = gameData.tileData(0, 0);
        var data1x0 = gameData.tileData(1, 0);
        var data0x1 = gameData.tileData(0, 1);
        var data1x1 = gameData.tileData(1, 1);
        data0x0.structureType(new TileTypeId(0));
        data1x0.structureType(new TileTypeId(1));
        data0x1.memory(Map.of(
                new PlayerId(0), new TileTypeId(2),
                new PlayerId(1), new TileTypeId(3)
        ));
        data1x1.structureType(new TileTypeId(4));
        try {
            fixture.execute(List.of(item), unit);
        } catch (ActionException _) {}
        verify(actionExecutor).execute(List.of(item), unit);
        assertThat(data0x0.memory()).isEmpty();
        assertThat(data1x0.memory())
                .hasSize(1)
                .containsEntry(new PlayerId(0), new TileTypeId(1));
        assertThat(data0x1.memory())
                .hasSize(1)
                .containsEntry(new PlayerId(0), new TileTypeId(2));
        assertThat(data1x1.memory())
                .hasSize(2)
                .containsEntry(new PlayerId(0), new TileTypeId(4))
                .containsEntry(new PlayerId(1), new TileTypeId(4));
    }

    @Test
    void asSeenBy() {
        var tileTypes = IntStream.range(0, 8).<TileType>mapToObj(_ -> mock()).toList();
        when(ruleset.tileTypes()).thenReturn(tileTypes);
        when(gameDataFactory.create(gameData)).then(_ -> {
            var copy = gameData(version);
            copy.createUnitData(new TileId(0, 0), new UnitTypeId(0));
            copy.createUnitData(new TileId(1, 1), new UnitTypeId(1));
            copy.tileData(1, 0).memory(Map.of(new PlayerId(0), new TileTypeId(0)));
            copy.tileData(0, 1).structureType(new TileTypeId(1)).tileType(new TileTypeId(2));
            return copy;
        });
        when(queryEvaluator.evaluate(any(VisionQuery.class))).then(invocation -> {
            var query = invocation.<VisionQuery>getArgument(0);
            assertThat(query.subject().id().intValue()).isZero();
            return List
                    .<NodeId>of(new TileId(0, 0), new TileId(1, 1), new UnitId(0))
                    .contains(query.target().id());

        });
        var redacted = fixture.asSeenBy(fixture.players().getFirst());
        assertThat(redacted.units()).singleElement().satisfies(unit -> assertThat(unit.id().intValue()).isZero());
        assertThat(redacted.tile(1, 0).orElseThrow()).returns(tileTypes.get(0), Tile::type);
        assertThat(redacted.tile(0, 1).orElseThrow()).returns(tileTypes.get(2), Tile::type);
    }

    private static GameData gameData(Version version) {
        var playerData0 = new PlayerData().playerId(new PlayerId(0)).commander(new CommanderId(0));
        var playerData1 = new PlayerData().playerId(new PlayerId(1)).commander(new CommanderId(0));
        var tileData0 = new TileData().tileId(new TileId(0, 0));
        var tileData1 = new TileData().tileId(new TileId(1, 0));
        var tileData2 = new TileData().tileId(new TileId(0, 1));
        var tileData3 = new TileData().tileId(new TileId(1, 1));
        var gameData = new GameData().version(version);
        gameData.players().addAll(List.of(playerData0, playerData1));
        gameData.tiles().addAll(List.of(tileData0, tileData1, tileData2, tileData3));
        gameData.activePlayer(new PlayerId(0)).width(2);
        return gameData;
    }

}
