package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ConstantAwareMethodResolverTest {

    private interface Helper {

        int getFoo();

        Helper setFoo(int foo);

        void frobnicate();

    }

    private interface ConstantHelper extends Helper, ScriptConstant {}

    @Mock
    private EvaluationContext context;

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        this.random = random;
        lenient().when(context.getTypeConverter()).thenReturn(new StandardTypeConverter());
    }

    private final ConstantAwareMethodResolver fixture = new ConstantAwareMethodResolver();

    @MethodSource
    @ParameterizedTest
    void resolve(Object targetObject, String name, List<Class<?>> types, @Nullable Method method) throws Exception {
        var argumentTypes = types.stream().map(TypeDescriptor::valueOf).toList();
        var executor = fixture.resolve(context, targetObject, name, argumentTypes);
        if (method == null) {
            assertThat(executor).isNull();
        } else {
            assertThat(executor).isNotNull();
            var arguments = types.stream().map(random::get).toArray();
            executor.execute(context, targetObject, arguments);
            method.invoke(verify(targetObject), arguments);
        }
    }

    private static Stream<Arguments> resolve() throws NoSuchMethodException {
        var helper = mock(Helper.class);
        var constant = mock(ConstantHelper.class);
        return Stream.of(
                arguments(helper, "getFoo", List.of(), Helper.class.getMethod("getFoo")),
                arguments(helper, "setFoo", List.of(int.class), Helper.class.getMethod("setFoo", int.class)),
                arguments(helper, "frobnicate", List.of(), Helper.class.getMethod("frobnicate")),
                arguments(constant, "getFoo", List.of(), Helper.class.getMethod("getFoo")),
                arguments(constant, "setFoo", List.of(int.class), null),
                arguments(constant, "frobnicate", List.of(), null),
                arguments(4.2, "isFinite", List.of(double.class), null) // static method called on instance
        );
    }

}
