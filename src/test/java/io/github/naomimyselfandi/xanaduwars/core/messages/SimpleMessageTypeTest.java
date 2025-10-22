package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.message.MessageType;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class SimpleMessageTypeTest {

    private record FooQuery(int x, int y, int z) implements SimpleQuery<String> {

        @Override
        public @NotNull String defaultValue(@NotNull ScriptRuntime runtime) {
            throw new UnsupportedOperationException();
        }

    }

    private record BarEvent(int a, int b, int c) implements SimpleEvent {}

    @MethodSource
    @ParameterizedTest
    void name(Class<?> type, String expected) {
        assertThat(new SimpleMessageType(type)).returns(expected, MessageType::name).hasToString(expected);
    }

    @MethodSource
    @ParameterizedTest
    void properties(Class<?> type, List<String> expected) {
        assertThat(new SimpleMessageType(type).properties()).isUnmodifiable().isEqualTo(expected);
    }

    @Test
    void call(SeededRng random) {
        var x = random.nextInt();
        var y = random.nextInt();
        var z = random.nextInt();
        var a = random.nextInt();
        var b = random.nextInt();
        var c = random.nextInt();
        assertThat(new SimpleMessageType(FooQuery.class).call(x, y, z)).isEqualTo(new FooQuery(x, y, z));
        assertThat(new SimpleMessageType(BarEvent.class).call(a, b, c)).isEqualTo(new BarEvent(a, b, c));
    }

    @Test
    void call_WhenTheReflectiveCallFails_ThenWrapsTheException(SeededRng random) {
        var x = random.nextInt();
        var y = random.nextInt();
        assertThatThrownBy(() -> new SimpleMessageType(FooQuery.class).call(x, y))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed creating FooQuery from [%d, %d].", x, y);
    }

    private static Stream<Arguments> name() {
        return Stream.of(arguments(FooQuery.class, "Foo"),arguments(BarEvent.class, "Bar"));
    }

    private static Stream<Arguments> properties() {
        return Stream.of(
                arguments(FooQuery.class, List.of("x", "y", "z")),
                arguments(BarEvent.class, List.of("a", "b", "c"))
        );
    }

}
