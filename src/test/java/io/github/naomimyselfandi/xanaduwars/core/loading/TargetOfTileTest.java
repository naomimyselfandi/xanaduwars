package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.CommandException;
import io.github.naomimyselfandi.xanaduwars.core.model.GameState;
import io.github.naomimyselfandi.xanaduwars.core.model.Tile;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TargetOfTileTest {

    @Mock
    private GameState gameState;

    @Mock
    private Unit actor;

    @Mock
    private Tile tile;

    private final TargetOfTile fixture = TargetOfTile.TILE;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void unpack(boolean tileExists, SeededRng random) throws CommandException {
        int x = random.nextInt();
        int y = random.not(x);
        when(actor.getGameState()).thenReturn(gameState);
        var jsonNode = new ObjectNode(JsonNodeFactory.instance, Map.of(
                "x", new IntNode(x),
                "y", new IntNode(y)
        ));
        if (tileExists) {
            when(gameState.getTile(x, y)).thenReturn(tile);
            assertThat(fixture.unpack(actor, jsonNode)).isEqualTo(tile);
        } else {
            assertThatThrownBy(() -> fixture.unpack(actor, jsonNode))
                    .isInstanceOf(CommandException.class)
                    .hasMessage("Target is out of bounds.");
        }
    }

    @MethodSource
    @ParameterizedTest
    void unpack_WhenTheInputIsMalformed_ThenNull(JsonNode jsonNode) {
        assertThatThrownBy(() -> fixture.unpack(actor, jsonNode))
                .isInstanceOf(CommandException.class)
                .hasMessage("Expected an object with two int values, x and y.");
        verifyNoInteractions(actor);
    }

    @Test
    void validate() {
        assertThat(fixture.validate(actor, tile)).isTrue();
        verifyNoInteractions(actor, tile);
    }

    @Test
    void propose() {
        when(actor.getGameState()).thenReturn(gameState);
        when(gameState.getTiles()).thenReturn(List.of(tile));
        assertThat(fixture.propose(actor)).containsExactly(tile);
    }

    @Test
    void pack(SeededRng random) {
        int x = random.nextInt();
        int y = random.not(x);
        when(tile.getX()).thenReturn(x);
        when(tile.getY()).thenReturn(y);
        assertThat(fixture.pack(tile)).isEqualTo(new ObjectNode(JsonNodeFactory.instance, Map.of(
                "x", new IntNode(x),
                "y", new IntNode(y)
        )));
    }

    private static Stream<JsonNode> unpack_WhenTheInputIsMalformed_ThenNull() {
        return Stream.of(
                NullNode.getInstance(),
                BooleanNode.TRUE,
                BooleanNode.FALSE,
                new ObjectNode(JsonNodeFactory.instance, Map.of(
                        "x", new DoubleNode(41.3),
                        "y", new IntNode(612)
                )),
                new ObjectNode(JsonNodeFactory.instance, Map.of(
                        "x", new IntNode(413),
                        "y", new DoubleNode(61.2)
                )),
                new ObjectNode(JsonNodeFactory.instance, Map.of(
                        "x", new IntNode(413),
                        "z", new IntNode(612)
                )),
                new ObjectNode(JsonNodeFactory.instance, Map.of(
                        "y", new IntNode(413),
                        "z", new IntNode(612)
                ))
        );
    }


}
