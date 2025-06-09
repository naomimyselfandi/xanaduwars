package io.github.naomimyselfandi.xanaduwars.core.scripting;

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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StatementWithWhileLoopTest {

    @Mock
    private EvaluationContext context;

    @Mock
    private Expression condition;

    @Mock
    private Statement a, b, c;

    private StatementWithWhileLoop fixture;

    @BeforeEach
    void setup() {
        fixture = new StatementWithWhileLoop(condition, List.of(a, b, c));
    }

    @NullSource
    @SneakyThrows
    @ParameterizedTest
    @ValueSource(booleans = false)
    void execute(@Nullable Boolean falseOrNull) {
        when(condition.getValue(context, Boolean.class)).thenReturn(true, true, falseOrNull);
        fixture.execute(context);
        var inOrder = inOrder(a, b, c);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        inOrder.verify(c).execute(context);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        inOrder.verify(c).execute(context);
        verifyNoMoreInteractions(a, b, c);
    }

    @Test
    @SneakyThrows
    void execute_CanBreak() {
        when(condition.getValue(context, Boolean.class)).thenReturn(true);
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
        var inOrder = inOrder(a, b, c);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        inOrder.verify(c).execute(context);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        verifyNoMoreInteractions(a, b, c);
    }

    @NullSource
    @SneakyThrows
    @ParameterizedTest
    @ValueSource(booleans = false)
    void execute_CanContinue(@Nullable Boolean falseOrNull) {
        when(condition.getValue(context, Boolean.class)).thenReturn(true, true, true, falseOrNull);
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
        var inOrder = inOrder(a, b, c);
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
        when(condition.getValue(context, Boolean.class)).thenReturn(true);
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
        when(condition.getValue(context, Boolean.class)).thenReturn(true);
        doThrow(new Statement.Continue(depth)).when(b).execute(context);
        assertThatThrownBy(() -> fixture.execute(context)).isEqualTo(new Statement.Continue(depth - 1));
        var inOrder = inOrder(a, b, c);
        inOrder.verify(a).execute(context);
        inOrder.verify(b).execute(context);
        verifyNoMoreInteractions(a, b, c);
    }

    @Test
    void testToString(SeededRng random) {
        var conditionString = random.nextString();
        when(condition.getExpressionString()).thenReturn(conditionString);
        when(a.toString()).thenReturn(random.nextString());
        when(b.toString()).thenReturn(random.nextString());
        when(c.toString()).thenReturn(random.nextString());
        assertThat(fixture).hasToString("while %s; %s; %s; %s; done", conditionString, a, b, c);
    }

}
