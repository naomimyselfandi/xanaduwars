package io.github.naomimyselfandi.xanaduwars.core.data;

import io.github.naomimyselfandi.xanaduwars.core.wrapper.Percent;
import io.github.naomimyselfandi.xanaduwars.core.Resource;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class GameMapTest {

    private final GameMap fixture = new GameMap().name("M" + ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));

    @BeforeEach
    void setup() {
        fixture.id(UUID.randomUUID());
    }

    @Test
    void testHashCode() {
        assertThat(fixture).hasSameHashCodeAs(GameMap.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void playerCount(int playerCount) {
        assertThat(fixture.playerCount(playerCount)).isSameAs(fixture);
        assertThat(fixture.players())
                .hasSize(playerCount)
                .allSatisfy(player -> {
                    for (var field : PlayerData.Fields.values()) {
                        var _ = switch (field) {
                            case playerId -> assertThat(fixture.players().get(player.playerId().intValue()))
                                    .isSameAs(player);
                            case team -> assertThat(player.team()).isEqualTo(player.playerId().intValue());
                            case defeated -> assertThat(player.defeated()).isFalse();
                            case playerType -> assertThat(player.playerType()).isZero();
                            case resources -> assertThat(player.resources())
                                    .containsOnlyKeys(Resource.values())
                                    .values()
                                    .allSatisfy(value -> assertThat(value).isZero());
                            case knownSpells -> assertThat(player.knownSpells()).hasSize(0);
                            case activeSpells -> assertThat(player.activeSpells()).hasSize(0);
                        };
                    }
                });
        assertThat(fixture.players().stream().map(PlayerData::playerId))
                .containsExactlyElementsOf(() -> IntStream.range(0, playerCount).mapToObj(PlayerId::new).iterator());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            2,3
            4,2
            """)
    void dimensions(int width, int height) {
        assertThat(fixture.dimensions(width, height)).isSameAs(fixture);
        assertThat(fixture.tiles().stream().map(TileData::tileId))
                .containsExactlyElementsOf(() -> IntStream
                        .range(0, height)
                        .boxed()
                        .flatMap(y -> IntStream.range(0, width).mapToObj(x -> new TileId(x, y)))
                        .iterator());
        assertThat(fixture.tiles()).allSatisfy(tile -> {
            for (var field : TileData.Fields.values()) {
                var _ = switch (field) {
                    case tileId -> null;
                    case tileType -> assertThat(tile.tileType()).isZero();
                    case structureType -> {
                        assertThat(tile.structureType()).isNull();
                        yield null;
                    }
                    case owner -> {
                        assertThat(tile.owner()).isNull();
                        yield null;
                    }
                    case hitpoints -> assertThat(tile.hitpoints()).isEqualTo(Percent.FULL);
                    case construction -> {
                        assertThat(tile.construction()).isNull();
                        yield null;
                    }
                    case memory -> assertThat(tile.memory()).hasSize(0);
                };
            }
        });
        assertThat(fixture.nextUnitId()).isZero();
    }

    @Test
    void dimensions_ClearsExistingData() {
        fixture.dimensions(3, 2).createUnitData(new TileId(0, 0), 0);
        fixture.dimensions(2, 4);
        assertThat(fixture.tiles()).hasSize(8);
        assertThat(fixture.units()).isEmpty();
    }

}
