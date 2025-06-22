package io.github.naomimyselfandi.xanaduwars.core.scripting.json;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StatementWithWhileLoopTest {

    private interface Helper {}

    @Mock
    private Helper helper;

    @Mock
    private EvaluationContext context;

    @Mock
    private Expression condition;

    @Mock
    private Statement body;

    private StatementWithWhileLoop fixture;

    @BeforeEach
    void setup() {
        fixture = new StatementWithWhileLoop(condition, body);
    }

    @NullSource
    @SneakyThrows
    @ParameterizedTest
    @ValueSource(booleans = false)
    void execute(@Nullable Boolean falseOrNull) {
        when(condition.getValue(context, Boolean.class))
                .thenReturn(true)
                .thenReturn(true).thenReturn(falseOrNull);
        when(body.execute(context, Helper.class)).thenReturn(Statement.PROCEED);
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(Statement.PROCEED);
        verify(body, times(2)).execute(context, Helper.class);
    }

    @Test
    @SneakyThrows
    void execute_CanReturn() {
        when(condition.getValue(context, Boolean.class)).thenReturn(true);
        when(body.execute(context, Helper.class))
                .thenReturn(Statement.PROCEED)
                .thenReturn(Statement.PROCEED)
                .thenReturn(helper);
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(helper);
        verify(body, times(3)).execute(context, Helper.class);
    }

    @Test
    @SneakyThrows
    void execute_CanNull() {
        when(condition.getValue(context, Boolean.class)).thenReturn(true);
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
        when(condition.getValue(context, Boolean.class)).thenReturn(true);
        when(body.execute(context, Helper.class)).thenReturn(Statement.PROCEED).thenReturn(new Statement.Break(1));
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(Statement.PROCEED);
        verify(body, times(2)).execute(context, Helper.class);
    }

    @NullSource
    @SneakyThrows
    @ParameterizedTest
    @ValueSource(booleans = false)
    void execute_CanContinue(@Nullable Boolean falseOrNull) {
        when(condition.getValue(context, Boolean.class))
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(falseOrNull);
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
        when(condition.getValue(context, Boolean.class)).thenReturn(true);
        when(body.execute(context, Helper.class)).thenReturn(Statement.PROCEED).thenReturn(new Statement.Break(depth));
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(new Statement.Break(depth - 1));
        verify(body, times(2)).execute(context, Helper.class);
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void execute_CanContinueMultipleLevels(int depth) {
        when(condition.getValue(context, Boolean.class)).thenReturn(true);
        when(body.execute(context, Helper.class)).thenReturn(Statement.PROCEED).thenReturn(new Statement.Continue(depth));
        assertThat(fixture.execute(context, Helper.class)).isEqualTo(new Statement.Continue(depth - 1));
        verify(body, times(2)).execute(context, Helper.class);
    }

    @Test
    void testToString(SeededRng random) {
        var s0 = random.nextString();
        var s1 = random.nextString();
        when(body.toString()).thenReturn(s0 + "\n" + s1);
        when(condition.getExpressionString()).thenReturn(random.nextString());
        assertThat(fixture).hasToString("""
                while %s:
                  %s
                  %s""".formatted(condition.getExpressionString(), s0, s1));
    }

}
