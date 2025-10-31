package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class FunctionInvocationHandlerTest {

    private interface Helper extends BinaryOperator<String>, Supplier<String> {}

    @Mock
    private Function function;

    @InjectMocks
    private FunctionInvocationHandler fixture;

    private Helper proxy;

    @BeforeEach
    void setup() {
        var interfaces = new Class<?>[]{Helper.class};
        proxy = (Helper) Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, fixture);
    }

    @Test
    void invoke(SeededRng random) {
        var foo = random.nextString();
        var bar = random.not(foo);
        var baz = random.not(foo, bar);
        when(function.call(foo, bar)).thenReturn(baz);
        assertThat(proxy.apply(foo, bar)).isEqualTo(baz);
    }

    @Test
    void invoke_DefaultMethod(SeededRng random) {
        var foo = random.nextString();
        var bar = random.not(foo);
        var baz = random.not(foo, bar);
        when(function.call(foo, bar)).thenReturn(baz);
        assertThat(proxy.andThen(String::hashCode).apply(foo, bar)).isEqualTo(baz.hashCode());
    }

    @Test
    void invoke_WithoutArguments(SeededRng random) {
        // proxies sometimes send null instead of an empty argument list
        var foo = random.nextString();
        when(function.call()).thenReturn(foo);
        assertThat(proxy.get()).isEqualTo(foo);
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void invoke_Equals() {
        assertThat(proxy).isEqualTo(proxy);
        assertThat(proxy).isNotEqualTo(mock(Helper.class));
    }

    @Test
    void invoke_HashCode() {
        assertThat(proxy.hashCode()).isEqualTo(System.identityHashCode(proxy));
    }

    @Test
    void invoke_ToString() {
        assertThat(proxy).hasToString(Objects.toIdentityString(proxy));
    }

    /*
    @Test
    void convert(SeededRng random) {
        var sourceType = TypeDescriptor.forObject(function);
        var targetType = TypeDescriptor.valueOf(Helper.class);
        var foo = random.nextString();
        var bar = random.nextString();
        var baz = random.nextString();
        when(function.call(foo, bar)).thenReturn(baz);
        assertThat(fixture.convert(function, sourceType, targetType)).isInstanceOfSatisfying(Helper.class, proxy -> {
            assertThat(proxy.apply(foo, bar)).isEqualTo(baz);
            assertThat(proxy.andThen(String::hashCode).apply(foo, bar)).isEqualTo(baz.hashCode());
            assertThat(proxy.hashCode()).isEqualTo(System.identityHashCode(proxy));
            assertThat(proxy).hasToString(Objects.toIdentityString(proxy));
            assertThat(proxy).isEqualTo(proxy);
            assertThat(proxy).isNotEqualTo(fixture.convert(function, sourceType, targetType));
        });
    }*/

    private static Stream<Arguments> matches() {
        abstract class FunctionImpl implements Function {}
        abstract class RunnableImpl implements Runnable {}
        return Stream.of(
                arguments(Function.class, Runnable.class, true),
                arguments(Function.class, IntSupplier.class, true),
                arguments(Function.class, IntConsumer.class, true),
                arguments(FunctionImpl.class, Runnable.class, true),
                arguments(Function.class, Iterator.class, false),
                arguments(Function.class, RunnableImpl.class, false)
        );
    }

}
