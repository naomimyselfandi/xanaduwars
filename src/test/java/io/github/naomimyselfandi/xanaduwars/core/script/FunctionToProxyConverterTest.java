package io.github.naomimyselfandi.xanaduwars.core.script;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.TypeDescriptor;

import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
class FunctionToProxyConverterTest {

    @Mock
    private Function function;

    private FunctionToProxyConverter fixture;

    @BeforeEach
    void setup() {
        fixture = new FunctionToProxyConverter();
    }

    @Test
    void convert() {
        var sourceType = TypeDescriptor.forObject(function);
        var targetType = TypeDescriptor.valueOf(Runnable.class);
        assertThat(fixture.convert(function, sourceType, targetType))
                .isNotNull()
                .returns(new FunctionInvocationHandler(function), Proxy::getInvocationHandler);
    }

    @Test
    void getConvertibleTypes() {
        assertThat(fixture.getConvertibleTypes()).isNull();
    }

    @MethodSource
    @ParameterizedTest
    void matches(Class<?> source, Class<?> target, boolean expected) {
        var sourceType = TypeDescriptor.valueOf(source);
        var targetType = TypeDescriptor.valueOf(target);
        assertThat(fixture.matches(sourceType, targetType)).isEqualTo(expected);
    }

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
