package io.github.naomimyselfandi.xanaduwars.core.scripting;

import io.github.naomimyselfandi.xanaduwars.ext.None;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QueryEvaluatorImplTest {

    @Mock
    private GlobalRuleSource globals;

    @Mock
    private ContextFactory contextFactory;

    @Mock
    private ScriptSelector scriptSelector;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private QueryEvaluatorImpl fixture;

    @BeforeEach
    void setup() {
        when(conversionService.convert(any(), eq(Integer.class))).then(invocation -> {
            var source = invocation.getArgument(0);
            return (source == null || source.equals("null")) ? null : Integer.valueOf(source.toString());
        });
    }

    @Test
    void evaluate() {
        var query = setup(10, "#value * 4", "#value + 2");
        assertThat(fixture.evaluate(globals, query)).isEqualTo(42);
    }

    @Test
    void evaluate_AppliesConversions() {
        var query = setup(15, "#value / 2", "#value * 3");
        assertThat(fixture.evaluate(globals, query)).isEqualTo(21);
    }

    @Test
    void evaluate_ToleratesNull() {
        var query = setup(15, "#value / 2", "null", "#value * 3");
        assertThat(fixture.evaluate(globals, query)).isEqualTo(21);
    }

    @Test
    void evaluate_DoesNotTryToConvertNone() {
        var list = new ArrayList<>();
        var query = new BarEvent(list);
        setup(query, "subject.add(40)", "subject.add(2)");
        assertThat(fixture.evaluate(globals, query)).isEqualTo(None.NONE);
        assertThat(list).containsExactly(40, 2);
    }

    private Query<Integer> setup(int value, String... scripts) {
        var query = new FooQuery(new Object(), value);
        setup(query, scripts);
        return query;
    }

    private void setup(Query<?> query, String... scripts) {
        var context = new StandardEvaluationContext(query);
        when(contextFactory.create(globals, query)).thenReturn(context);
        var parser = new SpelExpressionParser();
        when(scriptSelector.select(globals, query)).thenReturn(Arrays
                .stream(scripts)
                .map(parser::parseExpression)
                .<Script>map(e -> c -> {
                    var v = e.getValue(c);
                    return v == null ? null : v.toString();
                }).toList());
    }

}
