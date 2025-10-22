package io.github.naomimyselfandi.xanaduwars.core.message;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.script.Script;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.TypeDescriptor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AbstractMessageBusTest {

    @Mock
    private EventListener eventListener;

    @Mock
    private Script when1, then1, when2, then2, when3, then3;

    @Mock
    private MessageType messageType;

    private String foo, bar;

    private Object fooValue, barValue;

    private AbstractMessageBus fixture;

    @BeforeEach
    void setup(SeededRng random) {
        foo = random.get();
        bar = random.not(foo);
        fooValue = new Object();
        barValue = new Object();
        lenient().when(messageType.properties()).thenReturn(List.of(foo, bar));
        var rule1 = new Rule(messageType, when1, then1);
        var rule2 = new Rule(messageType, when2, then2);
        fixture = new AbstractMessageBus() {

            @Override
            protected @NotNull Stream<Rule> getGlobalRules() {
                return Stream.of(rule1, rule2);
            }

            @Override
            public @Nullable Object lookup(@NotNull String name) {
                return fail();
            }

        };
    }

    @Test
    void evaluate(SeededRng random) {
        var value1 = random.nextInt();
        var value2 = random.not(value1);
        var value3 = random.not(value1, value2);
        when(when1.<Boolean>executeNotNull(fixture, arguments(value1))).thenReturn(true);
        when(then1.execute(fixture, arguments(value1), TypeDescriptor.valueOf(Integer.class))).thenReturn(value2);
        when(when2.<Boolean>executeNotNull(fixture, arguments(value2))).thenReturn(true);
        when(then2.execute(fixture, arguments(value2), TypeDescriptor.valueOf(Integer.class))).thenReturn(value3);
        assertThat(fixture.evaluate(query(value1, fooValue, barValue))).isEqualTo(value3);
    }

    @Test
    void evaluate_WhenAContextualRuleIsAvailable_ThenUsesIt(SeededRng random) {
        fooValue = (ContextualRuleSource) () -> Stream.of(new Rule(messageType, when3, then3));
        var value1 = random.nextInt();
        var value2 = random.not(value1);
        var value3 = random.not(value1, value2);
        var value4 = random.not(value1, value2, value3);
        when(when1.<Boolean>executeNotNull(fixture, arguments(value1))).thenReturn(true);
        when(then1.execute(fixture, arguments(value1), TypeDescriptor.valueOf(Integer.class))).thenReturn(value2);
        when(when2.<Boolean>executeNotNull(fixture, arguments(value2))).thenReturn(true);
        when(then2.execute(fixture, arguments(value2), TypeDescriptor.valueOf(Integer.class))).thenReturn(value3);
        when(when3.<Boolean>executeNotNull(fixture, arguments(value3))).thenReturn(true);
        when(then3.execute(fixture, arguments(value3), TypeDescriptor.valueOf(Integer.class))).thenReturn(value4);
        assertThat(fixture.evaluate(query(value1, fooValue, barValue))).isEqualTo(value4);
    }

    @Test
    void evaluate_WhenARuleDeclinesTheMessage_ThenDoesNotUseIt(SeededRng random) {
        var value1 = random.nextInt();
        var value2 = random.not(value1);
        when(when1.<Boolean>executeNotNull(fixture, arguments(value1))).thenReturn(true);
        when(then1.execute(fixture, arguments(value1), TypeDescriptor.valueOf(Integer.class))).thenReturn(value2);
        when(when2.<Boolean>executeNotNull(fixture, arguments(value2))).thenReturn(false);
        assertThat(fixture.evaluate(query(value1, fooValue, barValue))).isEqualTo(value2);
        verifyNoInteractions(then2);
    }

    @Test
    void evaluate_WhenARuleReturnsNull_ThenIgnoresIt(SeededRng random) {
        var value1 = random.nextInt();
        var value2 = random.not(value1);
        when(when1.<Boolean>executeNotNull(fixture, arguments(value1))).thenReturn(true);
        when(then1.execute(fixture, arguments(value1), TypeDescriptor.valueOf(Integer.class))).thenReturn(null);
        when(when2.<Boolean>executeNotNull(fixture, arguments(value1))).thenReturn(true);
        when(then2.execute(fixture, arguments(value1), TypeDescriptor.valueOf(Integer.class))).thenReturn(value2);
        assertThat(fixture.evaluate(query(value1, fooValue, barValue))).isEqualTo(value2);
    }

    @Test
    void evaluate_WhenAQueryHasAlreadyBeenEvaluated_ThenCachesTheResult(SeededRng random) {
        var value1 = random.nextInt();
        var value2 = random.not(value1);
        var value3 = random.not(value1, value2);
        when(when1.<Boolean>executeNotNull(fixture, arguments(value1)))
                .thenReturn(true)
                .thenThrow(AssertionError.class);
        when(then1.execute(fixture, arguments(value1), TypeDescriptor.valueOf(Integer.class)))
                .thenReturn(value2)
                .thenThrow(AssertionError.class);
        when(when2.<Boolean>executeNotNull(fixture, arguments(value2)))
                .thenReturn(true)
                .thenThrow(AssertionError.class);
        when(then2.execute(fixture, arguments(value2), TypeDescriptor.valueOf(Integer.class)))
                .thenReturn(value3)
                .thenThrow(AssertionError.class);
        assertThat(fixture.evaluate(query(value1, fooValue, barValue))).isEqualTo(value3);
        assertThat(fixture.evaluate(query(value1, fooValue, barValue))).isEqualTo(value3);
    }

    @Test
    void evaluate_WhenAQueryIsCached_OtherQueriesAreNotAffected(SeededRng random) {
        var value1 = random.nextInt();
        var value2 = random.not(value1);
        var value3 = random.not(value1, value2);
        var value4 = random.not(value1, value2, value3);
        var value5 = random.not(value1, value2, value3, value4);
        var value6 = random.not(value1, value2, value3, value4, value5);
        when(when1.<Boolean>executeNotNull(fixture, arguments(value1))).thenReturn(true);
        when(then1.execute(fixture, arguments(value1), TypeDescriptor.valueOf(Integer.class))).thenReturn(value2);
        when(when2.<Boolean>executeNotNull(fixture, arguments(value2))).thenReturn(true);
        when(then2.execute(fixture, arguments(value2), TypeDescriptor.valueOf(Integer.class))).thenReturn(value3);
        var originalFooValue = fooValue;
        var originalBarValue = barValue;
        fooValue = new Object();
        barValue = new Object();
        when(when1.<Boolean>executeNotNull(fixture, arguments(value4))).thenReturn(true);
        when(then1.execute(fixture, arguments(value4), TypeDescriptor.valueOf(Integer.class))).thenReturn(value5);
        when(when2.<Boolean>executeNotNull(fixture, arguments(value5))).thenReturn(true);
        when(then2.execute(fixture, arguments(value5), TypeDescriptor.valueOf(Integer.class))).thenReturn(value6);
        assertThat(fixture.evaluate(query(value1, originalFooValue, originalBarValue))).isEqualTo(value3);
        assertThat(fixture.evaluate(query(value4, fooValue, barValue))).isEqualTo(value6);
    }

    @Test
    void clearQueryCache(SeededRng random) {
        var value1 = random.nextInt();
        var value2 = random.not(value1);
        var value3 = random.not(value1, value2);
        var value4 = random.not(value1, value2, value3);
        when(when1.<Boolean>executeNotNull(fixture, arguments(value1)))
                .thenReturn(true);
        when(then1.execute(fixture, arguments(value1), TypeDescriptor.valueOf(Integer.class)))
                .thenReturn(value2);
        when(when2.<Boolean>executeNotNull(fixture, arguments(value2)))
                .thenReturn(true);
        when(then2.execute(fixture, arguments(value2), TypeDescriptor.valueOf(Integer.class)))
                .thenReturn(value3)
                .thenReturn(value4);
        assertThat(fixture.evaluate(query(value1, fooValue, barValue))).isEqualTo(value3);
        assertThat(fixture.evaluate(query(value1, fooValue, barValue))).isEqualTo(value3);
        fixture.clearQueryCache();
        assertThat(fixture.evaluate(query(value1, fooValue, barValue))).isEqualTo(value4);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 3})
    void evaluate_WhenTheArgumentCountIsIncorrect_ThenThrows(int count, SeededRng random) {
        var name = random.nextString();
        when(messageType.name()).thenReturn(name);
        var arguments = IntStream.range(0, count).mapToObj(_ -> new Object()).toArray();
        assertThatThrownBy(() -> fixture.evaluate(query(0, arguments)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A message of type '%s' needs 2 arguments, not %d.", name, count);
    }

    @Test
    void dispatch() {
        var arguments = arguments(null);
        when(when1.<Boolean>executeNotNull(fixture, arguments)).thenReturn(true);
        when(when2.<Boolean>executeNotNull(fixture, arguments)).thenReturn(true);
        fixture.dispatch(event(fooValue, barValue));
        var inOrder = inOrder(then1, then2);
        inOrder.verify(then1).execute(fixture, arguments, null);
        inOrder.verify(then2).execute(fixture, arguments, null);
    }

    @Test
    void dispatch_WhenAContextualRuleIsAvailable_ThenUsesIt() {
        fooValue = (ContextualRuleSource) () -> Stream.of(new Rule(messageType, when3, then3));
        var arguments = arguments(null);
        when(when1.<Boolean>executeNotNull(fixture, arguments)).thenReturn(true);
        when(when2.<Boolean>executeNotNull(fixture, arguments)).thenReturn(true);
        when(when3.<Boolean>executeNotNull(fixture, arguments)).thenReturn(true);
        fixture.dispatch(event(fooValue, barValue));
        var inOrder = inOrder(then1, then2, then3);
        inOrder.verify(then1).execute(fixture, arguments, null);
        inOrder.verify(then2).execute(fixture, arguments, null);
        inOrder.verify(then3).execute(fixture, arguments, null);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,true
            true,false
            false,true
            false,false
            """)
    void dispatch_WhenARuleDeclinesTheMessage_ThenDoesNotUseIt(boolean rule1, boolean rule2) {
        var arguments = arguments(null);
        when(when1.<Boolean>executeNotNull(fixture, arguments)).thenReturn(rule1);
        when(when2.<Boolean>executeNotNull(fixture, arguments)).thenReturn(rule2);
        fixture.dispatch(event(fooValue, barValue));
        verify(then1, times(rule1 ? 1 : 0)).execute(fixture, arguments, null);
        verify(then2, times(rule2 ? 1 : 0)).execute(fixture, arguments, null);
    }

    @Test
    void dispatch_WhenAQueryIsCached_ThenClearsTheCache(SeededRng random) {
        var value1 = random.nextInt();
        var value2 = random.not(value1);
        var value3 = random.not(value1, value2);
        var value4 = random.not(value1, value2, value3);
        when(when1.<Boolean>executeNotNull(eq(fixture), any()))
                .thenReturn(true);
        when(then1.execute(fixture, arguments(value1), TypeDescriptor.valueOf(Integer.class)))
                .thenReturn(value2);
        when(when2.<Boolean>executeNotNull(fixture, arguments(value2)))
                .thenReturn(true);
        when(then2.execute(fixture, arguments(value2), TypeDescriptor.valueOf(Integer.class)))
                .thenReturn(value3)
                .thenReturn(value4);
        assertThat(fixture.evaluate(query(value1, fooValue, barValue))).isEqualTo(value3);
        assertThat(fixture.evaluate(query(value1, fooValue, barValue))).isEqualTo(value3);
        var event = mock(Event.class);
        var eventType = mock(MessageType.class);
        when(event.type()).thenReturn(eventType);
        fixture.dispatch(event);
        assertThat(fixture.evaluate(query(value1, fooValue, barValue))).isEqualTo(value4);
    }

    @Test
    void attachEventListener() {
        try (var _ = fixture.attachEventListener(eventListener)) {
            var event = event(fooValue, barValue);
            var arguments = arguments(null);
            when(when1.<Boolean>executeNotNull(fixture, arguments)).thenReturn(true);
            when(when2.<Boolean>executeNotNull(fixture, arguments)).thenReturn(true);
            fixture.dispatch(event);
            var inOrder = inOrder(eventListener, then1, then2);
            inOrder.verify(eventListener).receive(event);
            inOrder.verify(then1).execute(fixture, arguments, null);
            inOrder.verify(then2).execute(fixture, arguments, null);
        }
    }

    @Test
    void attachEventListener_WhenTheCleanupCallbackIsCalled_ThenTheListenerIsRemoved() {
        fixture.attachEventListener(eventListener).close();
        var arguments = arguments(null);
        when(when1.<Boolean>executeNotNull(fixture, arguments)).thenReturn(true);
        when(when2.<Boolean>executeNotNull(fixture, arguments)).thenReturn(true);
        fixture.dispatch(event(fooValue, barValue));
        verifyNoInteractions(eventListener);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 3})
    void dispatch_WhenTheArgumentCountIsIncorrect_ThenThrows(int count, SeededRng random) {
        var name = random.nextString();
        when(messageType.name()).thenReturn(name);
        var arguments = IntStream.range(0, count).mapToObj(_ -> new Object()).toArray();
        assertThatThrownBy(() -> fixture.dispatch(event(arguments)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A message of type '%s' needs 2 arguments, not %d.", name, count);
    }

    private Event event(Object... arguments) {
        record TestEvent(MessageType type, List<Object> arguments) implements Event {}
        return new TestEvent(messageType, Arrays.asList(arguments));
    }

    private <T> Query<T> query(T defaultValue, Object... arguments) {
        record TestQuery<T>(MessageType type, List<Object> arguments, T defaultValue) implements Query<T> {

            @Override
            public @NotNull T defaultValue(@NotNull ScriptRuntime runtime) {
                return defaultValue;
            }

            @Override
            public @NotNull TypeDescriptor resultType() {
                return TypeDescriptor.forObject(defaultValue);
            }

        }
        return new TestQuery<>(messageType, Arrays.asList(arguments), defaultValue);
    }

    private Map<String, Object> arguments(@Nullable Object value) {
        var arguments = new HashMap<String, Object>(3);
        arguments.put(foo, fooValue);
        arguments.put(bar, barValue);
        arguments.put("value", value);
        return arguments;
    }

}
