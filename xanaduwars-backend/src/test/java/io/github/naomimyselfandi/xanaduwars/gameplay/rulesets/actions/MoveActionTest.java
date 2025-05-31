package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.Direction;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Path;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.PathValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class MoveActionTest {

    @Mock
    private GameState gameState;

    @Mock
    private Unit unit;

    @Mock
    private Tile a, b, c;

    @Mock
    private MoveLogic moveLogic;

    private MoveAction fixture;

    @BeforeEach
    void setup() {
        lenient().when(unit.gameState()).thenReturn(gameState);
        lenient().when(unit.tile()).thenReturn(Optional.of(a));
        fixture = new MoveAction().moveLogic(moveLogic);
        // Prevent exceptions from Path constructor
        lenient().when(a.distance(b)).thenReturn(1);
        lenient().when(b.distance(c)).thenReturn(1);
    }

    @Test
    void enumerateTargets() {
        assertThat(fixture.enumerateTargets(gameState)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test(boolean ok, SeededRng random) {
        var d1 = random.pick(Direction.values());
        var d2 = random.pick(Direction.values());
        when(a.step(d1)).thenReturn(Optional.of(b));
        when(b.step(d2)).thenReturn(Optional.of(c));
        when(gameState.evaluate(new PathValidation(unit, new Path(a, List.of(b, c))))).thenReturn(ok);
        assertThat(fixture.test(unit, List.of(d1, d2))).isEqualTo(ok);
    }

    @Test
    void test_WhenNoDirectionsAreGiven_ThenFalse() {
        assertThat(fixture.test(unit, List.of())).isFalse();
    }

    @RepeatedTest(2)
    void test_WhenThePathFallsOffTheMap_ThenFalse(RepetitionInfo repetitionInfo, SeededRng random) {
        var d1 = random.pick(Direction.values());
        var d2 = random.pick(Direction.values());
        if (repetitionInfo.getCurrentRepetition() == 0) {
            when(a.step(d1)).thenReturn(Optional.empty());
        } else {
            when(a.step(d1)).thenReturn(Optional.of(b));
            when(b.step(d2)).thenReturn(Optional.empty());
        }
        assertThat(fixture.test(unit, List.of(d1, d2))).isFalse();
    }

    @EnumSource
    @ParameterizedTest
    void execute(Execution execution, SeededRng random) {
        var d1 = random.pick(Direction.values());
        var d2 = random.pick(Direction.values());
        when(a.step(d1)).thenReturn(Optional.of(b));
        when(b.step(d2)).thenReturn(Optional.of(c));
        when(moveLogic.execute(new Path(a, List.of(b, c)), unit)).thenReturn(execution);
        assertThat(fixture.execute(unit, List.of(d1, d2))).isEqualTo(execution);
    }

}
