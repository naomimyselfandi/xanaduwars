package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.scripting.FooQuery;
import io.github.naomimyselfandi.xanaduwars.core.scripting.QueryEvaluator;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class GameStateImplTest {

    @Mock
    private Ruleset ruleset;

    private GameStateData gameStateData;

    @Mock
    private QueryEvaluator queryEvaluator;

    private PlayerData alice, bob;

    private TileData tile0, tile1;

    private UnitData unit0, unit1;

    private boolean preview;

    private GameStateImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        alice = random.nextPlayerData().id(new PlayerId(0));
        bob = random.nextPlayerData().id(new PlayerId(1));
        tile0 = random.nextTileData().id(new TileId(0, 0));
        tile1 = random.nextTileData().id(new TileId(1, 0));
        unit0 = random.nextUnitData();
        unit1 = random.nextUnitData();
        preview = random.nextBoolean();
        gameStateData = new GameStateData()
                .id(random.get())
                .version(random.get())
                .turn(random.get())
                .playerData(List.of(alice, bob))
                .tileData(List.of(tile0, tile1))
                .unitData(List.of(unit0, unit1));
        fixture = new GameStateImpl(ruleset, gameStateData, queryEvaluator, preview);
    }

    @Test
    void id() {
        assertThat(fixture.id()).isEqualTo(gameStateData.id());
    }

    @Test
    void evaluate(SeededRng random) {
        var query = new FooQuery(new Object(), random.nextInt());
        var value = random.nextInt();
        when(queryEvaluator.evaluate(ruleset, query)).thenReturn(value);
        assertThat(fixture.evaluate(query)).isEqualTo(value);
    }

    @RepeatedTest(4)
    void preview() {
        assertThat(fixture.preview()).isEqualTo(preview);
    }

    @Test
    void pass() {
        gameStateData.pass(false);
        fixture.pass();
        assertThat(gameStateData.pass()).isTrue();
    }

    @Test
    void players() {
        assertThat(fixture.players()).containsExactly(
                new PlayerImpl(alice, fixture, ruleset),
                new PlayerImpl(bob, fixture, ruleset)
        );
    }

    @Test
    void player() {
        assertThat(fixture.player(new PlayerId(0))).isEqualTo(new PlayerImpl(alice, fixture, ruleset));
        assertThat(fixture.player(new PlayerId(1))).isEqualTo(new PlayerImpl(bob, fixture, ruleset));
    }

    @Test
    void activePlayer(SeededRng random) {
        gameStateData.turn(new Turn(random.nextInt(0, Short.MAX_VALUE) * 2));
        assertThat(fixture.activePlayer()).isEqualTo(new PlayerImpl(alice, fixture, ruleset));
        gameStateData.turn(new Turn(random.nextInt(0, Short.MAX_VALUE) * 2 + 1));
        assertThat(fixture.activePlayer()).isEqualTo(new PlayerImpl(bob, fixture, ruleset));
    }

    @Test
    void tiles() {
        assertThat(fixture.tiles()).containsExactly(
                new TileImpl(tile0, fixture, ruleset, new CreatorImpl(gameStateData)),
                new TileImpl(tile1, fixture, ruleset, new CreatorImpl(gameStateData))
        );
    }

    @Test
    void tile() {
        assertThat(fixture.tile(new TileId(0, 0)))
                .isEqualTo(new TileImpl(tile0, fixture, ruleset, new CreatorImpl(gameStateData)));
        assertThat(fixture.tile(new TileId(1, 0)))
                .isEqualTo(new TileImpl(tile1, fixture, ruleset, new CreatorImpl(gameStateData)));
    }

    @Test
    void maybeTile() {
        assertThat(fixture.maybeTile(new TileId(0, 0)))
                .isEqualTo(new TileImpl(tile0, fixture, ruleset, new CreatorImpl(gameStateData)));
        assertThat(fixture.maybeTile(new TileId(1, 0)))
                .isEqualTo(new TileImpl(tile1, fixture, ruleset, new CreatorImpl(gameStateData)));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            -1,0
            0,-1
            2,0
            1,1
            """)
    void maybeTile_WhenTheTileIsOutOfBounds_ThenNull(int x, int y) {
        assertThat(fixture.maybeTile(new TileId(x, y))).isNull();
    }

    @Test
    void structures(SeededRng random) {
        tile0.structureData(null);
        var structureData = random.nextStructureData();
        tile1.structureData(structureData);
        var tile = fixture.tile(new TileId(1, 0));
        assertThat(fixture.structures()).containsExactly(new StructureImpl(structureData, tile, ruleset));
    }

    @Test
    void units() {
        assertThat(fixture.units()).containsExactly(
                new UnitImpl(unit0, fixture, ruleset),
                new UnitImpl(unit1, fixture, ruleset)
        );
    }

    @Test
    void unit() {
        assertThat(fixture.unit(unit0.id())).isEqualTo(new UnitImpl(unit0, fixture, ruleset));
        assertThat(fixture.unit(unit1.id())).isEqualTo(new UnitImpl(unit1, fixture, ruleset));
    }

}
