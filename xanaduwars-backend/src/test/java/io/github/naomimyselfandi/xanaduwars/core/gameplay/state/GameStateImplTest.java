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
        alice = random.nextPlayerData().setId(new PlayerId(0));
        bob = random.nextPlayerData().setId(new PlayerId(1));
        tile0 = random.nextTileData().setId(new TileId(0, 0));
        tile1 = random.nextTileData().setId(new TileId(1, 0));
        unit0 = random.nextUnitData();
        unit1 = random.nextUnitData();
        preview = random.nextBoolean();
        gameStateData = new GameStateData()
                .setId(random.get())
                .setVersion(random.get())
                .setTurn(random.get())
                .setPlayerData(List.of(alice, bob))
                .setTileData(List.of(tile0, tile1))
                .setUnitData(List.of(unit0, unit1));
        fixture = new GameStateImpl(ruleset, gameStateData, queryEvaluator, preview);
    }

    @Test
    void getId() {
        assertThat(fixture.getId()).isEqualTo(gameStateData.getId());
    }

    @Test
    void evaluate(SeededRng random) {
        var query = new FooQuery(new Object(), random.nextInt());
        var value = random.nextInt();
        when(queryEvaluator.evaluate(ruleset, query)).thenReturn(value);
        assertThat(fixture.evaluate(query)).isEqualTo(value);
    }

    @RepeatedTest(4)
    void isPreview() {
        assertThat(fixture.isPreview()).isEqualTo(preview);
    }

    @Test
    void pass() {
        gameStateData.setPass(false);
        fixture.pass();
        assertThat(gameStateData.isPass()).isTrue();
    }

    @Test
    void getPlayers() {
        assertThat(fixture.getPlayers()).containsExactly(
                new PlayerImpl(alice, fixture, ruleset),
                new PlayerImpl(bob, fixture, ruleset)
        );
    }

    @Test
    void getPlayer() {
        assertThat(fixture.getPlayer(new PlayerId(0))).isEqualTo(new PlayerImpl(alice, fixture, ruleset));
        assertThat(fixture.getPlayer(new PlayerId(1))).isEqualTo(new PlayerImpl(bob, fixture, ruleset));
    }

    @Test
    void getActivePlayer(SeededRng random) {
        gameStateData.setTurn(new Turn(random.nextInt(0, Short.MAX_VALUE) * 2));
        assertThat(fixture.getActivePlayer()).isEqualTo(new PlayerImpl(alice, fixture, ruleset));
        gameStateData.setTurn(new Turn(random.nextInt(0, Short.MAX_VALUE) * 2 + 1));
        assertThat(fixture.getActivePlayer()).isEqualTo(new PlayerImpl(bob, fixture, ruleset));
    }

    @Test
    void getTiles() {
        assertThat(fixture.getTiles()).containsExactly(
                new TileImpl(tile0, fixture, ruleset, new CreatorImpl(gameStateData)),
                new TileImpl(tile1, fixture, ruleset, new CreatorImpl(gameStateData))
        );
    }

    @Test
    void getTile() {
        assertThat(fixture.getTile(new TileId(0, 0)))
                .isEqualTo(new TileImpl(tile0, fixture, ruleset, new CreatorImpl(gameStateData)));
        assertThat(fixture.getTile(new TileId(1, 0)))
                .isEqualTo(new TileImpl(tile1, fixture, ruleset, new CreatorImpl(gameStateData)));
    }

    @Test
    void findTile() {
        assertThat(fixture.findTile(new TileId(0, 0)))
                .isEqualTo(new TileImpl(tile0, fixture, ruleset, new CreatorImpl(gameStateData)));
        assertThat(fixture.findTile(new TileId(1, 0)))
                .isEqualTo(new TileImpl(tile1, fixture, ruleset, new CreatorImpl(gameStateData)));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            -1,0
            0,-1
            2,0
            1,1
            """)
    void findTile_WhenTheTileIsOutOfBounds_ThenNull(int x, int y) {
        assertThat(fixture.findTile(new TileId(x, y))).isNull();
    }

    @Test
    void getStructures(SeededRng random) {
        tile0.setStructureData(null);
        var structureData = random.nextStructureData();
        tile1.setStructureData(structureData);
        var tile = fixture.getTile(new TileId(1, 0));
        assertThat(fixture.getStructures()).containsExactly(new StructureImpl(structureData, tile, ruleset));
    }

    @Test
    void getUnits() {
        assertThat(fixture.getUnits()).containsExactly(
                new UnitImpl(unit0, fixture, ruleset),
                new UnitImpl(unit1, fixture, ruleset)
        );
    }

    @Test
    void getUnit() {
        assertThat(fixture.getUnit(unit0.getId())).isEqualTo(new UnitImpl(unit0, fixture, ruleset));
        assertThat(fixture.getUnit(unit1.getId())).isEqualTo(new UnitImpl(unit1, fixture, ruleset));
    }

}
