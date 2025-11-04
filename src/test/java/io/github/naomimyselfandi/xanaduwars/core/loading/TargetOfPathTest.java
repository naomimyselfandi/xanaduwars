package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TargetOfPathTest {

    private static final List<List<Integer>> CAPACITY_USED_BY_TILE = List.of(
            List.of(1, 1, 2, 2, 2),
            List.of(1, 1, 6, 1, 1),
            List.of(1, 1, 1, 1, 1)
    );

    @Mock
    private Ability ability;

    @Mock
    private Player player;

    @Mock
    private Unit actor, anotherUnit;

    private TargetOfPath fixture;

    @BeforeEach
    void setup() {
        fixture = new TargetOfPath() {

            @Override
            public double getCapacityUsed(@NotNull Unit unit, @NotNull List<Tile> path) {
                return path
                        .stream()
                        .mapToDouble(tile -> CAPACITY_USED_BY_TILE.get(tile.getY()).get(tile.getX()))
                        .sum();
            }

            @Override
            public double getCapacity(@NotNull Unit unit) {
                assertThat(unit).isEqualTo(actor);
                return 6;
            }

            @Override
            public boolean doCustomValidation(@NotNull Unit unit, @NotNull List<Tile> path) {
                return path.size() != 2 && path.size() == Set.copyOf(path).size();
            }

        };
    }

    @Test
    void unpack() throws CommandException {
        var origin = tile(0, 0);
        when(actor.getLocation()).thenReturn(origin);
        var jsonNode = new ArrayNode(JsonNodeFactory.instance, List.of(
                new TextNode("EAST"),
                new TextNode("SOUTH"),
                new TextNode("WEST"),
                new TextNode("SOUTH"),
                new TextNode("EAST"),
                new TextNode("EAST"),
                new TextNode("NORTH")
        ));
        assertThat(fixture.unpack(actor, jsonNode)).containsExactly(
                tile(1, 0),
                tile(1, 1),
                tile(0, 1),
                tile(0, 2),
                tile(1, 2),
                tile(2, 2),
                tile(2, 1)
        );
    }

    @Test
    void unpack_WhenThePathIsOutOfBounds_ThenThrows() {
        var origin = tile(0, 0);
        when(actor.getLocation()).thenReturn(origin);
        var jsonNode = new ArrayNode(JsonNodeFactory.instance, List.of(
                new TextNode("EAST"),
                new TextNode("SOUTH"),
                new TextNode("WEST"),
                new TextNode("WEST")
        ));
        assertThatThrownBy(() -> fixture.unpack(actor, jsonNode))
                .isInstanceOf(CommandException.class)
                .hasMessage("Path crosses map boundary.");
    }

    @Test
    void unpack_WhenTheActorIsNotAUnit_Throws() {
        var jsonNode = new ArrayNode(JsonNodeFactory.instance, List.of(
                new TextNode("EAST"),
                new TextNode("SOUTH"),
                new TextNode("WEST"),
                new TextNode("SOUTH"),
                new TextNode("EAST"),
                new TextNode("EAST"),
                new TextNode("NORTH")
        ));
        assertThatThrownBy(() -> fixture.unpack(player, jsonNode))
                .isInstanceOf(CommandException.class)
                .hasMessage("Not on a tile.");
    }

    @Test
    void unpack_WhenTheActorIsNotOnATile_Throws() {
        var jsonNode = new ArrayNode(JsonNodeFactory.instance, List.of(
                new TextNode("EAST"),
                new TextNode("SOUTH"),
                new TextNode("WEST"),
                new TextNode("SOUTH"),
                new TextNode("EAST"),
                new TextNode("EAST"),
                new TextNode("NORTH")
        ));
        when(actor.getLocation()).thenReturn(anotherUnit);
        assertThatThrownBy(() -> fixture.unpack(actor, jsonNode))
                .isInstanceOf(CommandException.class)
                .hasMessage("Not on a tile.");
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            '"42"',Expected an array of directions.
            '"NORTH"',Expected an array of directions.
            '["NORTH", "NORTHEAST", "EAST"]',Unknown direction in path.
            """)
    void unpack_WhenTheInputIsMalformed_ThenThrows(String json, String message) throws JsonProcessingException {
        var origin = tile(0, 0);
        when(actor.getLocation()).thenReturn(origin);
        var jsonNode = new ObjectMapper().readValue(json, JsonNode.class);
        assertThatThrownBy(() -> fixture.unpack(actor, jsonNode))
                .isInstanceOf(CommandException.class)
                .hasMessage(message);
    }

    @MethodSource
    @ParameterizedTest
    void validate(List<Tile> path, boolean expected) {
        var origin = tile(0, 0);
        when(actor.getLocation()).thenReturn(origin);
        assertThat(fixture.validate(actor, path)).isEqualTo(expected);
    }

    private static Stream<Arguments> validate() {
        return Stream.of(
                arguments(List.of(tile(1, 0), tile(2, 0), tile(3, 0)), true),
                arguments(List.of(tile(1, 0), tile(2, 0), tile(3, 0), tile(4, 0)), false),
                arguments(List.of(tile(1, 0), tile(2, 0)), false)
        );
    }

    @Test
    void propose() {
        var origin = tile(0, 0);
        when(actor.getLocation()).thenReturn(origin);
        assertThat(fixture.propose(actor)).containsExactly(
                tile(0, 0),
                tile(1, 0),
                tile(3, 0),
                tile(0, 1),
                tile(3, 1),
                tile(0, 2),
                tile(1, 2),
                tile(2, 2),
                tile(3, 2),
                tile(4, 2)
        );
    }

    @Test
    void propose_WhenTheActorIsNotAUnit_ThenEmpty() {
        assertThat(fixture.propose(player)).isEmpty();
    }

    @Test
    void propose_WhenTheActorIsNotOnATile_ThenEmpty() {
        when(actor.getLocation()).thenReturn(anotherUnit);
        assertThat(fixture.propose(actor)).isEmpty();
    }

    @Test
    void pack(SeededRng random) {
        var tile = tile(random.nextInt(100), random.nextInt(100));
        assertThat(fixture.pack(tile)).isEqualTo(TargetOfTile.TILE.pack(tile));
    }

    private static Tile tile(int x, int y) {
        interface Holder {
            Map<String, Tile> CACHE = new HashMap<>();
        }
        return Holder.CACHE.computeIfAbsent("(%d, %d)".formatted(x, y), n -> {
            var tile = mock(Tile.class, n);
            when(tile.getX()).thenReturn(x);
            when(tile.getY()).thenReturn(y);
            when(tile.step(any())).then(invocation -> {
                var direction = invocation.<Direction>getArgument(0);
                return switch (direction) {
                    case NORTH -> y == 0 ? null : tile(x, y - 1);
                    case EAST -> x == 4 ? null : tile(x + 1, y);
                    case SOUTH -> y == 2 ? null : tile(x, y + 1);
                    case WEST -> x == 0 ? null : tile(x - 1, y);
                };
            });
            return tile;
        });
    }

}
