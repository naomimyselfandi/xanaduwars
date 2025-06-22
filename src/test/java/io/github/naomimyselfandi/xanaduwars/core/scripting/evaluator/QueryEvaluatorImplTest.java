package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.scripting.*;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.expression.EvaluationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class QueryEvaluatorImplTest {

    @Mock
    private Runnable runnable;

    private Object game;

    @Mock
    private GlobalRuleSource globals;

    @Mock
    private ContextFactory contextFactory;

    @Mock
    private ScriptSelector scriptSelector;

    private QueryEvaluatorImpl fixture;

    @BeforeEach
    void setup() {
        game = new Object();
        fixture = new QueryEvaluatorImpl(globals, contextFactory, scriptSelector) {};
    }

    @Test
    void evaluate() {
        var query = new FooQuery(null, 10);
        setup(query, v -> v - 3, v -> v * 6);
        assertThat(fixture.evaluate(game, query)).isEqualTo(42);
    }

    @Test
    void evaluate_WhenAScriptReturnsNull_ThenIgnoresIt() {
        var query = new FooQuery(null, 15);
        setup(query, v -> v / 5, _ -> null, v -> v * 7);
        assertThat(fixture.evaluate(game, query)).isEqualTo(21);
    }

    @Test
    void evaluate_WhenAScriptReturnsAnUnsuccessfulResult_ThenAborts(SeededRng random) {
        var fail = random.<Result.Fail>get();
        var query = new BatQuery(null, Result.okay());
        setup(query, r -> { runnable.run(); return r; }, _ -> fail, _ -> Assertions.fail());
        assertThat(fixture.evaluate(game, query)).isEqualTo(fail);
        verify(runnable).run();
    }

    @SafeVarargs
    private <T> void setup(Query<T> query, UnaryOperator<T>... mockScripts) {
        var box = new HashMap<Void, T>();
        box.put(null, query.defaultValue());
        var context = mock(EvaluationContext.class);
        doAnswer(invocation -> {
            box.put(null, invocation.getArgument(1));
            return null;
        }).when(context).setVariable(eq("value"), any());
        when(contextFactory.create(globals, query, game)).thenReturn(context);
        var scripts = Arrays
                .stream(mockScripts)
                .<Script>map(callback -> (c, t) -> {
                    assertThat(c).isEqualTo(context);
                    assertThat(query.defaultValue()).isInstanceOf(t);
                    return callback.apply(box.get(null));
                })
                .toList();
        when(scriptSelector.select(globals, query)).thenReturn(scripts);
    }

}
