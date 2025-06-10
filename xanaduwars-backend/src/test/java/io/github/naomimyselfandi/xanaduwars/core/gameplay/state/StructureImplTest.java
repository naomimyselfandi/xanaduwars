package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.StructureData;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.VisionCheckQuery;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.VisionRangeQuery;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Action;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureType;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
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

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StructureImplTest {

    @Mock
    private Action action;

    @Mock
    private Rule rule;

    @Mock
    private Player player;

    @Mock
    private StructureType type;

    @Mock
    private Ruleset ruleset;

    @Mock
    private GameState gameState;

    private StructureData structureData;

    @Mock
    private Tile tile, anotherTile;

    private StructureImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        when(tile.getGameState()).thenReturn(gameState);
        structureData = random.nextStructureData();
        when(ruleset.getStructureType(structureData.getType())).thenReturn(type);
        fixture = new StructureImpl(structureData, tile, ruleset);
    }

    @Test
    void getGameState() {
        assertThat(fixture.getGameState()).isEqualTo(gameState);
    }

    @Test
    void getType() {
        assertThat(fixture.getType()).isEqualTo(type);
    }

    @Test
    void getTags(SeededRng random) {
        when(type.getTags()).thenReturn(Set.of(random.get(), random.get()));
        assertThat(fixture.getTags()).isEqualTo(type.getTags());
    }

    @Test
    void isComplete() {
        assertThat(fixture.setComplete(true)).isSameAs(fixture);
        assertThat(fixture.isComplete()).isTrue();
        assertThat(structureData.isComplete()).isTrue();
        assertThat(fixture.setComplete(false)).isSameAs(fixture);
        assertThat(fixture.isComplete()).isFalse();
        assertThat(structureData.isComplete()).isFalse();
    }

    @Test
    void owner(SeededRng random) {
        var playerId = random.<PlayerId>get();
        when(gameState.getPlayer(playerId)).thenReturn(player);
        structureData.setOwner(playerId);
        assertThat(fixture.getOwner()).isEqualTo(player);
    }

    @Test
    void owner_ToleratesNull() {
        structureData.setOwner(null);
        assertThat(fixture.getOwner()).isNull();
    }

    @Test
    void rules(SeededRng random) {
        when(player.rules()).then(_ -> Stream.of(rule));
        var playerId = random.<PlayerId>get();
        when(gameState.getPlayer(playerId)).thenReturn(player);
        structureData.setOwner(playerId);
        assertThat(fixture.rules()).containsExactly(rule);
    }

    @Test
    void rules_ToleratesNull() {
        structureData.setOwner(null);
        assertThat(fixture.rules()).isEmpty();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0
            1,1
            10,10
            50,50
            99,99
            100,100
            101,100
            200,100
            -1,0
            -100,0
            """)
    void hp(int hp, int actualHp) {
        assertThat(fixture.setHp(hp)).isSameAs(fixture);
        assertThat(fixture.getHp()).isEqualTo(actualHp);
        assertThat(structureData.getHp()).isEqualTo(actualHp);
    }

    @Test
    void vision(SeededRng random) {
        var range = random.nextIntNotNegative();
        when(gameState.evaluate(new VisionRangeQuery(fixture))).thenReturn(range);
        assertThat(fixture.getVision()).isEqualTo(range);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void canSee(boolean value) {
        when(gameState.evaluate(new VisionCheckQuery(fixture, anotherTile))).thenReturn(value);
        assertThat(fixture.canSee(anotherTile)).isEqualTo(value);
    }

    @Test
    void getMovementTable(SeededRng random) {
        when(type.getMovementTable()).thenReturn(random.get());
        assertThat(fixture.getMovementTable()).isEqualTo(type.getMovementTable());
    }

    @Test
    void getCover(SeededRng random) {
        when(type.getCover()).thenReturn(random.get());
        assertThat(fixture.getCover()).isEqualTo(fixture.getCover());
    }

    @Test
    void getAction() {
        when(ruleset.getDeploymentAction()).thenReturn(action);
        assertThat(fixture.getAction()).containsExactly(action);
    }

    @Test
    void getDistance(SeededRng random) {
        var distance = random.nextIntNotNegative();
        when(tile.getDistance((Physical) anotherTile)).thenReturn((double) distance);
        assertThat(fixture.getDistance(anotherTile)).isEqualTo(distance);
    }

    @Test
    void getTerrain() {
        assertThat(fixture.getTerrain()).isSameAs(fixture);
    }

    @Test
    void testToString(SeededRng random) {
        var x = random.nextIntNotNegative();
        var y = random.nextIntNotNegative();
        when(tile.getId()).thenReturn(new TileId(x, y));
        assertThat(fixture).hasToString("Structure[x=%d, y=%d, type=%s]", x, y, type);
    }

}
