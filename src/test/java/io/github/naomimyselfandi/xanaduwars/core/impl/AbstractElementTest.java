package io.github.naomimyselfandi.xanaduwars.core.impl;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.message.*;
import io.github.naomimyselfandi.xanaduwars.core.model.GameState;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Cleanup;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AbstractElementTest {

    @Mock
    private MessageType messageType;

    @Mock
    private Query<Object> query;

    @Mock
    private Event event;

    @Mock
    private ContextualRuleSource contextualRuleSource;

    @Mock
    private EventListener eventListener;

    @Mock
    private Cleanup cleanup;

    @Mock
    private GameState gameState;

    private AbstractElement fixture;

    @BeforeEach
    void setup() {
        fixture = new AbstractElement() {

            @Override
            @NotNull Stream<ContextualRuleSource> getAssociatedObjects() {
                return Stream.of(contextualRuleSource);
            }

        };
    }

    @Test
    void initialize() {
        fixture.initialize(gameState);
        assertThat(fixture.getGameState()).isEqualTo(gameState);
    }

    @Test
    void initialize_WhenTheElementIsAlreadyInitialized_ThenThrows() {
        fixture.initialize(gameState);
        assertThatThrownBy(() -> fixture.initialize(gameState))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("%s is already initialized!", fixture);
    }

    @Test
    void evaluate() {
        var value = new Object();
        when(gameState.evaluate(query)).thenReturn(value);
        fixture.initialize(gameState);
        assertThat(fixture.evaluate(query)).isEqualTo(value);
    }

    @Test
    void evaluate_WhenTheElementIsNotInitialized_ThenThrows(SeededRng random) {
        var name = random.nextString();
        when(query.type()).thenReturn(messageType);
        when(messageType.name()).thenReturn(name);
        assertThatThrownBy(() -> fixture.evaluate(query))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Evaluated %s query before initialization.", name);
    }

    @Test
    void dispatch() {
        fixture.initialize(gameState);
        fixture.dispatch(event);
        verify(gameState).dispatch(event);
    }

    @Test
    void dispatch_WhenTheElementIsNotInitialized_ThenDoesNothing() {
        assertThatCode(() -> fixture.dispatch(event)).doesNotThrowAnyException();
    }

    @Test
    void clearQueryCache() {
        fixture.initialize(gameState);
        fixture.clearQueryCache();
        verify(gameState).clearQueryCache();
    }

    @Test
    void clearQueryCache_WhenTheElementIsNotInitialized_ThenDoesNothing() {
        assertThatCode(fixture::clearQueryCache).doesNotThrowAnyException();
    }

    @Test
    void getContextualRules(SeededRng random) {
        var rule = random.<Rule>get();
        when(contextualRuleSource.getContextualRules()).then(_ -> Stream.of(rule));
        assertThat(fixture.getContextualRules()).containsExactly(rule);
    }

}
