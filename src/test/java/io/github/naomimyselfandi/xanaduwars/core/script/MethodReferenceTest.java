package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.*;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class MethodReferenceTest {

    @Mock
    private EvaluationContext context;

    @Mock
    private MethodExecutor methodExecutor;

    @Mock
    private MethodResolver resolver1, resolver2;

    private String name;

    private MethodReference fixture;

    @BeforeEach
    void setup(SeededRng random) {
        name = random.nextString();
        fixture = new MethodReference(context, name);
        lenient().when(context.getMethodResolvers()).thenReturn(List.of(resolver1, resolver2));
    }

    @RepeatedTest(2)
    void call(RepetitionInfo repetitionInfo, SeededRng random) throws AccessException {
        var resolver = context.getMethodResolvers().get(repetitionInfo.getCurrentRepetition() - 1);
        var foo = random.nextString();
        var bar = random.nextInt();
        var baz = new Object();
        when(resolver.resolve(context, foo, name, List.of(TypeDescriptor.forObject(bar)))).thenReturn(methodExecutor);
        when(methodExecutor.execute(context, foo, bar)).thenReturn(new TypedValue(baz));
        assertThat(fixture.call(foo, bar)).isEqualTo(baz);
    }

    @RepeatedTest(2)
    void call_ToleratesNullArguments(RepetitionInfo repetitionInfo, SeededRng random) throws AccessException {
        var resolver = context.getMethodResolvers().get(repetitionInfo.getCurrentRepetition() - 1);
        var foo = random.nextString();
        var bar = new Object();
        when(resolver.resolve(context, foo, name, Collections.singletonList(null))).thenReturn(methodExecutor);
        when(methodExecutor.execute(context, foo, (Object) null)).thenReturn(new TypedValue(bar));
        assertThat(fixture.call(foo, null)).isEqualTo(bar);
    }

    @RepeatedTest(2)
    void call_WhenAnExecutorThrows_ThenThrows(RepetitionInfo repetitionInfo, SeededRng random) throws AccessException {
        var exception = new AccessException(random.get());
        var resolver = context.getMethodResolvers().get(repetitionInfo.getCurrentRepetition() - 1);
        var foo = random.nextString();
        var bar = random.nextInt();
        when(resolver.resolve(context, foo, name, List.of(TypeDescriptor.forObject(bar)))).thenReturn(methodExecutor);
        when(methodExecutor.execute(context, foo, bar)).thenThrow(exception);
        assertThatThrownBy(() -> fixture.call(foo, bar))
                .isInstanceOf(EvaluationException.class)
                .hasMessage("Failed calling '%s' on %s with [%d].", name, foo, bar)
                .hasCause(exception);
    }

    @RepeatedTest(2)
    void call_WhenAResolverThrows_ThenThrows(RepetitionInfo repetitionInfo, SeededRng random) throws AccessException {
        var exception = new AccessException(random.get());
        var resolver = context.getMethodResolvers().get(repetitionInfo.getCurrentRepetition() - 1);
        var foo = random.nextString();
        var bar = random.nextInt();
        when(resolver.resolve(context, foo, name, List.of(TypeDescriptor.forObject(bar)))).thenThrow(exception);
        assertThatThrownBy(() -> fixture.call(foo, bar))
                .isInstanceOf(EvaluationException.class)
                .hasMessage("Failed calling '%s' on %s with [%d].", name, foo, bar)
                .hasCause(exception);
    }

    @Test
    void call_WhenNoResolverApplies_ThenThrows(SeededRng random) {
        var foo = random.nextString();
        var bar = random.nextInt();
        assertThatThrownBy(() -> fixture.call(foo, bar))
                .isInstanceOf(EvaluationException.class)
                .hasMessage("Failed calling '%s' on %s with [%d].", name, foo, bar);
    }

    @Test
    void call_WhenTheReceiverIsNull_ThenThrows(SeededRng random) {
        var foo = random.nextString();
        var bar = random.not(foo);
        assertThatThrownBy(() -> fixture.call(null, foo, bar))
                .isInstanceOf(EvaluationException.class)
                .hasMessage("Failed calling '%s' on null with [%s, %s].", name, foo, bar);
    }

    @Test
    void call_WhenNoArgumentsAreGiven_ThenThrows() {
        assertThatThrownBy(() -> fixture.call())
                .isInstanceOf(EvaluationException.class)
                .hasMessage("Tried to invoke '%s' method reference on nothing.", name);
    }

}
