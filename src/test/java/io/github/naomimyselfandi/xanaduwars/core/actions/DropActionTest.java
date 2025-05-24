package io.github.naomimyselfandi.xanaduwars.core.actions;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.*;
import io.github.naomimyselfandi.xanaduwars.core.queries.EntryQuery;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class DropActionTest {

    private Direction direction;

    @Mock
    private GameState gameState;

    @Mock
    private Unit cargo, transport, anotherTransport;

    @Mock
    private Tile tile, destination;

    private DropAction fixture;

    @BeforeEach
    void setup(SeededRandom random) {
        direction = random.pick(Direction.values());
        lenient().when(transport.gameState()).thenReturn(gameState);
        lenient().when(transport.tile()).thenReturn(Optional.of(tile));
        lenient().when(transport.cargo()).thenReturn(List.of(cargo));
        lenient().when(transport.tile()).thenReturn(Optional.of(tile));
        lenient().when(tile.step(direction)).thenReturn(Optional.of(destination));
        fixture = new DropAction(new Name("A" + random.nextInt(Integer.MAX_VALUE)), TagSet.EMPTY);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test(boolean ok, SeededRandom random) {
        var result = ok ? random.nextDouble() : Double.NaN;
        when(gameState.evaluate(new EntryQuery(cargo, destination), 0.0)).thenReturn(result);
        assertThat(fixture.test(transport, direction)).isEqualTo(ok);
    }

    @RepeatedTest(2)
    void test_WhenTheUnitHasNoCargo_ThenFalse() {
        when(transport.cargo()).thenReturn(List.of());
        assertThat(fixture.test(transport, direction)).isFalse();
    }

    @RepeatedTest(2)
    void test_WhenTheDestinationIsOutOfBounds_ThenFalse() {
        when(tile.step(direction)).thenReturn(Optional.empty());
        assertThat(fixture.test(transport, direction)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"TILE", "UNIT"})
    void execute(String destinationType, SeededRandom random) {
        var actualDestination = switch (destinationType) {
            case "TILE" -> destination;
            case "UNIT" -> {
                when(destination.unit()).thenReturn(Optional.of(anotherTransport));
                yield anotherTransport;
            }
            default -> Assertions.<Node>fail();
        };
        var result = random.nextDouble();
        when(gameState.evaluate(new EntryQuery(cargo, destination), 0.0)).thenReturn(result);
        assertThat(fixture.execute(transport, direction)).isEqualTo(Execution.SUCCESSFUL);
        verify(cargo).location(actualDestination);
    }

    @ParameterizedTest
    @ValueSource(strings = {"TILE", "UNIT"})
    void execute_Interruption(String destinationType) {
        if (destinationType.equals("UNIT")) {
            lenient().when(destination.unit()).thenReturn(Optional.of(anotherTransport));
        }
        when(gameState.evaluate(new EntryQuery(cargo, destination), 0.0)).thenReturn(Double.NaN);
        assertThat(fixture.execute(transport, direction)).isEqualTo(Execution.INTERRUPTED);
        verify(cargo, never()).location(anotherTransport);
    }

}
