package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.CleanupEvent;
import io.github.naomimyselfandi.xanaduwars.core.scripting.BarEvent;
import io.github.naomimyselfandi.xanaduwars.core.scripting.FooQuery;
import io.github.naomimyselfandi.xanaduwars.core.scripting.QueryEvaluator;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.core.scripting.None;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class QueryEvaluatorWithCleanerTest {

    @Mock
    private GameState gameState;

    @Mock
    private QueryEvaluator queryEvaluator;

    @Mock
    private Cleaner cleaner;

    @InjectMocks
    private QueryEvaluatorWithCleaner fixture;

    @Test
    void evaluate() {
        var event = new CleanupEvent();
        when(queryEvaluator.evaluate(gameState, event)).thenReturn(None.NONE);
        assertThat(fixture.evaluate(gameState, event)).isEqualTo(None.NONE);
        var inOrder = inOrder(queryEvaluator, cleaner);
        inOrder.verify(queryEvaluator).evaluate(gameState, event);
        inOrder.verify(cleaner).clean(gameState);
    }

    @Test
    void evaluate_WhenTheQueryIsNotAnEvent_ThenDoesNotClean(SeededRng random) {
        var value = random.nextInt();
        var query = random.<FooQuery>get();
        when(queryEvaluator.evaluate(gameState, query)).thenReturn(value);
        assertThat(fixture.evaluate(gameState, query)).isEqualTo(value);
        verifyNoInteractions(cleaner);
    }

    @Test
    void evaluate_WhenTheQueryIsNotACleanupEvent_ThenDoesNotClean(SeededRng random) {
        var event = random.<BarEvent>get();
        when(queryEvaluator.evaluate(gameState, event)).thenReturn(None.NONE);
        assertThat(fixture.evaluate(gameState, event)).isEqualTo(None.NONE);
        verify(queryEvaluator).evaluate(gameState, event);
        verifyNoInteractions(cleaner);
    }

    @Test
    void evaluate_DoesNotTryToCleanUnexpectedInputs() {
        var event = new CleanupEvent();
        fixture.evaluate(new Object(), event);
        verifyNoInteractions(cleaner);
    }

}
