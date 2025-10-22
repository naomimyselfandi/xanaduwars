package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class StatementOfBlockTest {

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private EvaluationContext context;

    @Mock
    private Statement foo, bar, baz;

    private Label spam, eggs;

    private StatementOfBlock fixture;

    @BeforeEach
    void setup(SeededRng random) {
        lenient().when(context.lookupVariable(StatementOfBlock.RESULT_VARIABLE)).thenReturn(Undefined.UNDEFINED);
        doAnswer(invocation -> {
            var name = invocation.<String>getArgument(0);
            var value = invocation.getArgument(1);
            lenient().when(context.lookupVariable(name)).thenReturn(value);
            return null;
        }).when(context).setVariable(any(), any());
        spam = random.get();
        eggs = random.not(spam);
        fixture = new StatementOfBlock(List.of(spam, foo, bar, eggs, baz));
    }

    @Test
    void execute() {
        assertThat(fixture.execute(runtime, context)).isNull();
        var inOrder = inOrder(foo, bar, baz);
        inOrder.verify(foo).execute(runtime, context);
        inOrder.verify(bar).execute(runtime, context);
        inOrder.verify(baz).execute(runtime, context);
    }

    @Test
    void execute_Goto() {
        when(bar.execute(runtime, context))
                .then(_ -> {
                    var target = context.lookupVariable(spam.name());
                    context.setVariable(StatementOfBlock.TARGET_VARIABLE, target);
                    return null;
                })
                .thenThrow(new AssertionError("bar"));
        when(foo.execute(runtime, context))
                .thenReturn(0)
                .then(_ -> {
                    var target = context.lookupVariable(eggs.name());
                    context.setVariable(StatementOfBlock.TARGET_VARIABLE, target);
                    return null;
                })
                .thenThrow(new AssertionError("foo"));
        assertThat(fixture.execute(runtime, context)).isNull();
        var inOrder = inOrder(foo, bar, baz);
        inOrder.verify(foo).execute(runtime, context);
        inOrder.verify(bar).execute(runtime, context);
        inOrder.verify(foo).execute(runtime, context);
        inOrder.verify(baz).execute(runtime, context);
    }

    @Test
    void execute_Return() {
        when(bar.execute(runtime, context)).then(_ -> {
            context.setVariable(StatementOfBlock.TARGET_VARIABLE, Integer.MAX_VALUE);
            return null;
        });
        assertThat(fixture.execute(runtime, context)).isNull();
        var inOrder = inOrder(foo, bar, baz);
        inOrder.verify(foo).execute(runtime, context);
        inOrder.verify(bar).execute(runtime, context);
        verifyNoInteractions(baz);
    }

    @Test
    void execute_Return(SeededRng random) {
        var value = random.nextString();
        when(bar.execute(runtime, context)).then(_ -> {
            context.setVariable(StatementOfBlock.TARGET_VARIABLE, Integer.MAX_VALUE);
            context.setVariable(StatementOfBlock.RESULT_VARIABLE, value);
            return null;
        });
        assertThat(fixture.execute(runtime, context)).isEqualTo(value);
        var inOrder = inOrder(foo, bar, baz);
        inOrder.verify(foo).execute(runtime, context);
        inOrder.verify(bar).execute(runtime, context);
        verifyNoInteractions(baz);
    }

}
