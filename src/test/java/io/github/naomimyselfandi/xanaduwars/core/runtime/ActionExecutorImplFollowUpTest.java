package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.xanaduwars.core.*;
import io.github.naomimyselfandi.xanaduwars.core.queries.StandardTargetValidation;
import io.github.naomimyselfandi.xanaduwars.core.Rule;
import io.github.naomimyselfandi.xanaduwars.core.Validation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ActionExecutorImplFollowUpTest {

    // This class is mostly here to fix a false negative in the coverage report.

    @Mock
    private Unit target;

    @Mock
    private Action<Tile, Unit> action;

    @Mock
    private GameState gameState;

    @BeforeEach
    void setup() {
        when(gameState.evaluate(any(Validation.class))).thenReturn(true);
        when(gameState.evaluate(any(Validation.class), Mockito.<Consumer<Rule<?, ?>>>any())).thenReturn(true);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PLAYER", "TILE", "UNIT"})
    void execute(String kind) {
        var element = switch (kind) {
            case "PLAYER" -> mock(Player.class);
            case "TILE" -> mock(Tile.class);
            case "UNIT" -> mock(Unit.class);
            default -> Assertions.<Element>fail();
        };
        when(element.gameState()).thenReturn(gameState);
        assertThatCode(() -> new ActionExecutorImpl().execute(List.of(), element)).doesNotThrowAnyException();
        if (element instanceof Unit unit) {
            verify(unit).canAct(false);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void execute() {
        var element = mock(Tile.class);
        when(element.gameState()).thenReturn(gameState);
        var item = new ActionItem<>(action, target);
        when(gameState.evaluate(eq(new StandardTargetValidation<>(action, element, target)), any(Consumer.class)))
                .thenReturn(false);
        try {
            new ActionExecutorImpl().execute(List.of(item), element);
        } catch (ActionException _) {}
    }

}
