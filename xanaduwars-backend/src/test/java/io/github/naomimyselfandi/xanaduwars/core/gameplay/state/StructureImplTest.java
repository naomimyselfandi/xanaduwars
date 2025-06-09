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
        when(tile.gameState()).thenReturn(gameState);
        structureData = random.nextStructureData();
        when(ruleset.structureType(structureData.type())).thenReturn(type);
        fixture = new StructureImpl(structureData, tile, ruleset);
    }

    @Test
    void gameState() {
        assertThat(fixture.gameState()).isEqualTo(gameState);
    }

    @Test
    void type() {
        assertThat(fixture.type()).isEqualTo(type);
    }

    @Test
    void tags(SeededRng random) {
        when(type.tags()).thenReturn(Set.of(random.get(), random.get()));
        assertThat(fixture.tags()).isEqualTo(type.tags());
    }

    @Test
    void complete() {
        assertThat(fixture.complete()).isFalse();
        assertThat(fixture.complete(true)).isSameAs(fixture);
        assertThat(fixture.complete()).isTrue();
        assertThat(structureData.complete()).isTrue();
        assertThat(fixture.complete(false)).isSameAs(fixture);
        assertThat(fixture.complete()).isFalse();
        assertThat(structureData.complete()).isFalse();
    }

    @Test
    void owner(SeededRng random) {
        var playerId = random.<PlayerId>get();
        when(gameState.player(playerId)).thenReturn(player);
        structureData.owner(playerId);
        assertThat(fixture.owner()).isEqualTo(player);
    }

    @Test
    void owner_ToleratesNull() {
        structureData.owner(null);
        assertThat(fixture.owner()).isNull();
    }

    @Test
    void rules(SeededRng random) {
        when(player.rules()).then(_ -> Stream.of(rule));
        var playerId = random.<PlayerId>get();
        when(gameState.player(playerId)).thenReturn(player);
        structureData.owner(playerId);
        assertThat(fixture.rules()).containsExactly(rule);
    }

    @Test
    void rules_ToleratesNull() {
        structureData.owner(null);
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
        assertThat(fixture.hp(hp)).isSameAs(fixture);
        assertThat(fixture.hp()).isEqualTo(actualHp);
        assertThat(structureData.hp()).isEqualTo(actualHp);
    }

    @Test
    void vision(SeededRng random) {
        var range = random.nextIntNotNegative();
        when(gameState.evaluate(new VisionRangeQuery(fixture))).thenReturn(range);
        assertThat(fixture.vision()).isEqualTo(range);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void canSee(boolean value) {
        when(gameState.evaluate(new VisionCheckQuery(fixture, anotherTile))).thenReturn(value);
        assertThat(fixture.canSee(anotherTile)).isEqualTo(value);
    }

    @Test
    void movementTable(SeededRng random) {
        when(type.movementTable()).thenReturn(random.get());
        assertThat(fixture.movementTable()).isEqualTo(type.movementTable());
    }

    @Test
    void cover(SeededRng random) {
        when(type.cover()).thenReturn(random.get());
        assertThat(fixture.cover()).isEqualTo(fixture.cover());
    }

    @Test
    void actions() {
        when(ruleset.deploymentAction()).thenReturn(action);
        assertThat(fixture.actions()).containsExactly(action);
    }

    @Test
    void distance(SeededRng random) {
        var distance = random.nextIntNotNegative();
        when(tile.distance((Physical) anotherTile)).thenReturn((double) distance);
        assertThat(fixture.distance(anotherTile)).isEqualTo(distance);
    }

    @Test
    void terrain() {
        assertThat(fixture.terrain()).isSameAs(fixture);
    }

    @Test
    void testToString(SeededRng random) {
        var x = random.nextIntNotNegative();
        var y = random.nextIntNotNegative();
        when(tile.id()).thenReturn(new TileId(x, y));
        assertThat(fixture).hasToString("Structure[x=%d, y=%d, type=%s]", x, y, type);
    }

}
