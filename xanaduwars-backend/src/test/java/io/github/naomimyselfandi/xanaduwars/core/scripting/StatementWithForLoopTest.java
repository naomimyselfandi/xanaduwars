package io.github.naomimyselfandi.xanaduwars.core.scripting;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StatementWithForLoopTest {

    @Mock
    private EvaluationContext context;

    private String variable;

    @Mock
    private Expression source;

    @Mock
    private Statement a, b, c;

    private StatementWithForLoop fixture;

    @BeforeEach
    void setup(SeededRng random) {
        variable = random.nextString();
        fixture = new StatementWithForLoop(variable, source, List.of(a, b, c));
    }

    @Test
    @SneakyThrows
    void execute_Iterable(SeededRng random) {
        var foo = random.nextString();
        var bar = random.nextString();
        when(source.getValue(context)).thenReturn(List.of(foo, bar));
        fixture.execute(context);
        var inOrder = inOrder(context, a, b, c);
        inOrder.verify(context).setVariable(variable, foo);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        inOrder.verify(c).execute(context);
        inOrder.verify(context).setVariable(variable, bar);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        inOrder.verify(c).execute(context);
    }

    @Test
    @SneakyThrows
    void execute_Stream(SeededRng random) {
        var foo = random.nextString();
        var bar = random.nextString();
        when(source.getValue(context)).then(_ -> Stream.of(foo, bar));
        fixture.execute(context);
        var inOrder = inOrder(context, a, b, c);
        inOrder.verify(context).setVariable(variable, foo);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        inOrder.verify(c).execute(context);
        inOrder.verify(context).setVariable(variable, bar);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        inOrder.verify(c).execute(context);
    }

    @Test
    @SneakyThrows
    void execute_Map(SeededRng random) {
        var foo = random.nextString();
        var spam = random.nextString();
        var bar = random.nextString();
        var eggs = random.nextString();
        var map = new LinkedHashMap<>();
        map.put(foo, spam);
        map.put(bar, eggs);
        when(source.getValue(context)).thenReturn(map);
        fixture.execute(context);
        var inOrder = inOrder(context, a, b, c);
        inOrder.verify(context).setVariable(variable, Map.entry(foo, spam));
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        inOrder.verify(c).execute(context);
        inOrder.verify(context).setVariable(variable, Map.entry(bar, eggs));
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        inOrder.verify(c).execute(context);
    }

    @Test
    @SneakyThrows
    void execute_WhenTheSourceIsNotIterable_ThenThrows(SeededRng random) {
        var notIterable = random.nextUUID();
        when(source.getValue(context)).thenReturn(notIterable);
        assertThatThrownBy(() -> fixture.execute(context))
                .isInstanceOf(ClassCastException.class)
                .hasMessage("`%s` evaluated to non-iterable %s.", source, notIterable);
        verifyNoInteractions(context, a, b, c);
    }

    @Test
    @SneakyThrows
    void execute_WhenTheSourceIsNull_ThenDoesNothing() {
        when(source.getValue(context)).thenReturn(null);
        assertThatCode(() -> fixture.execute(context)).doesNotThrowAnyException();
        verifyNoInteractions(context, a, b, c);
    }

    @Test
    @SneakyThrows
    void execute_CanBreak() {
        when(source.getValue(context)).thenReturn(Stream.generate(() -> 0));
        var state = new ArrayList<>();
        doAnswer(_ -> {
            if (state.isEmpty()) {
                state.add("");
                return null;
            } else {
                throw new Statement.Break(1);
            }
        }).when(b).execute(context);
        fixture.execute(context);
        var inOrder = inOrder(context, a, b, c);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        inOrder.verify(c).execute(context);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        verifyNoMoreInteractions(a, b, c);
    }

    @Test
    @SneakyThrows
    void execute_CanContinue() {
        when(source.getValue(context)).thenReturn(List.of(0, 1, 2));
        var state = new ArrayList<>();
        doAnswer(_ -> {
            state.add("");
            if (state.size() == 2) {
                throw new Statement.Continue(1);
            } else {
                return null;
            }
        }).when(b).execute(context);
        fixture.execute(context);
        var inOrder = inOrder(context, a, b, c);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        inOrder.verify(c).execute(context);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        inOrder.verify(c).execute(context);
        verifyNoMoreInteractions(a, b, c);
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void execute_CanBreakMultipleLevels(int depth) {
        when(source.getValue(context)).thenReturn(Stream.generate(() -> 0));
        doThrow(new Statement.Break(depth)).when(b).execute(context);
        assertThatThrownBy(() -> fixture.execute(context)).isEqualTo(new Statement.Break(depth - 1));
        var inOrder = inOrder(a, b, c);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        verifyNoMoreInteractions(a, b, c);
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void execute_CanContinueMultipleLevels(int depth) {
        when(source.getValue(context)).thenReturn(List.of(0, 1, 2));
        doThrow(new Statement.Continue(depth)).when(b).execute(context);
        assertThatThrownBy(() -> fixture.execute(context)).isEqualTo(new Statement.Continue(depth - 1));
        var inOrder = inOrder(a, b, c);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        verifyNoMoreInteractions(a, b, c);
    }

    @Test
    void testToString(SeededRng random) {
        var expressionString = random.nextString();
        when(source.getExpressionString()).thenReturn(expressionString);
        when(a.toString()).thenReturn(random.nextString());
        when(b.toString()).thenReturn(random.nextString());
        when(c.toString()).thenReturn(random.nextString());
        var template = "for %s : %s; %s; %s; %s; done";
        assertThat(fixture).hasToString(template, variable, expressionString, a, b, c);
    }

}
