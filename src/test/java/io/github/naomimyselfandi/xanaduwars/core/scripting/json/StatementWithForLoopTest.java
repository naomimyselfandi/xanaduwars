package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StatementWithForLoopTest {

    private interface Helper {}

    @Mock
    private Helper helper;

    @Mock
    private EvaluationContext context;

    private String variable;

    @Mock
    private Expression source;

    @Mock
    private Statement body;

    private StatementWithForLoop fixture;

    @BeforeEach
    void setup(SeededRng random) {
        variable = random.nextString();
        fixture = new StatementWithForLoop(variable, source, body);
    }

    @Test
    @SneakyThrows
    void execute_Iterable() {
        var foo = new Object();
        var bar = new Object();
        when(source.getValue(context)).thenReturn(List.of(foo, bar));
        when(body.execute(context, Helper.class)).thenReturn(Statement.PROCEED);
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(Statement.PROCEED);
        var inOrder = inOrder(context, body);
        inOrder.verify(context).setVariable(variable, foo);
        Statement statement1 = inOrder.verify(body);
        statement1.execute(context, Helper.class);
        inOrder.verify(context).setVariable(variable, bar);
        Statement statement = inOrder.verify(body);
        statement.execute(context, Helper.class);
    }

    @Test
    @SneakyThrows
    void execute_Stream() {
        var foo = new Object();
        var bar = new Object();
        when(source.getValue(context)).then(_ -> Stream.of(foo, bar));
        when(body.execute(context, Helper.class)).thenReturn(Statement.PROCEED);
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(Statement.PROCEED);
        var inOrder = inOrder(context, body);
        inOrder.verify(context).setVariable(variable, foo);
        Statement statement1 = inOrder.verify(body);
        statement1.execute(context, Helper.class);
        inOrder.verify(context).setVariable(variable, bar);
        Statement statement = inOrder.verify(body);
        statement.execute(context, Helper.class);
    }

    @Test
    @SneakyThrows
    void execute_Map() {
        var foo = new Object();
        var spam = new Object();
        var bar = new Object();
        var eggs = new Object();
        var map = new LinkedHashMap<>();
        map.put(foo, spam);
        map.put(bar, eggs);
        when(source.getValue(context)).thenReturn(map);
        when(body.execute(context, Helper.class)).thenReturn(Statement.PROCEED);
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(Statement.PROCEED);
        var inOrder = inOrder(context, body);
        inOrder.verify(context).setVariable(variable, Map.entry(foo, spam));
        Statement statement1 = inOrder.verify(body);
        statement1.execute(context, Helper.class);
        inOrder.verify(context).setVariable(variable, Map.entry(bar, eggs));
        Statement statement = inOrder.verify(body);
        statement.execute(context, Helper.class);
    }

    @Test
    @SneakyThrows
    void execute_WhenTheSourceIsNotIterable_ThenThrows(SeededRng random) {
        var notIterable = random.nextUUID();
        when(source.getValue(context)).thenReturn(notIterable);
        assertThatThrownBy(() -> fixture.execute(context, Helper.class))
                .isInstanceOf(ClassCastException.class)
                .hasMessage("`%s` evaluated to non-iterable %s.", source, notIterable);
        verifyNoInteractions(context, body);
    }

    @Test
    @SneakyThrows
    void execute_WhenTheSourceIsNull_ThenDoesNothing() {
        when(source.getValue(context)).thenReturn(null);
        assertThatCode(() -> fixture.execute(context, Helper.class)).doesNotThrowAnyException();
        verifyNoInteractions(context, body);
    }

    @Test
    @SneakyThrows
    void execute_CanReturn() {
        when(source.getValue(context)).thenReturn(Stream.generate(() -> 0));
        when(body.execute(context, Helper.class))
                .thenReturn(Statement.PROCEED)
                .thenReturn(Statement.PROCEED)
                .thenReturn(helper);
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(helper);
        verify(body, times(3)).execute(context, Helper.class);
    }

    @Test
    @SneakyThrows
    void execute_CanReturnNull() {
        when(source.getValue(context)).thenReturn(Stream.generate(() -> 0));
        when(body.execute(context, Helper.class))
                .thenReturn(Statement.PROCEED)
                .thenReturn(Statement.PROCEED)
                .thenReturn(null);
        assertThat(fixture.execute(context, Helper.class)).isNull();
        verify(body, times(3)).execute(context, Helper.class);
    }

    @Test
    @SneakyThrows
    void execute_CanBreak() {
        when(source.getValue(context)).thenReturn(Stream.generate(() -> null));
        when(body.execute(context, Helper.class)).thenReturn(Statement.PROCEED).thenReturn(new Statement.Break(1));
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(Statement.PROCEED);
        verify(body, times(2)).execute(context, Helper.class);
    }

    @Test
    @SneakyThrows
    void execute_CanContinue() {
        when(source.getValue(context)).thenReturn(List.of(0, 1, 2));
        when(body.execute(context, Helper.class))
                .thenReturn(Statement.PROCEED)
                .thenReturn(new Statement.Continue(1))
                .thenReturn(Statement.PROCEED);
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(Statement.PROCEED);
        verify(body, times(3)).execute(context, Helper.class);
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void execute_CanBreakMultipleLevels(int depth) {
        when(source.getValue(context)).thenReturn(Stream.generate(() -> 0));
        when(body.execute(context, Helper.class)).thenReturn(Statement.PROCEED).thenReturn(new Statement.Break(depth));
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(new Statement.Break(depth - 1));
        verify(body, times(2)).execute(context, Helper.class);
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void execute_CanContinueMultipleLevels(int depth) {
        when(source.getValue(context)).thenReturn(Stream.generate(() -> 0));
        when(body.execute(context, Helper.class)).thenReturn(Statement.PROCEED).thenReturn(new Statement.Continue(depth));
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(new Statement.Continue(depth - 1));
        verify(body, times(2)).execute(context, Helper.class);
    }

    @Test
    void testToString(SeededRng random) {
        var s0 = random.nextString();
        var s1 = random.nextString();
        when(body.toString()).thenReturn(s0 + "\n" + s1);
        when(source.getExpressionString()).thenReturn(random.nextString());
        assertThat(fixture).hasToString("""
                for %s in %s:
                  %s
                  %s""".formatted(variable, source.getExpressionString(), s0, s1));
    }

}
