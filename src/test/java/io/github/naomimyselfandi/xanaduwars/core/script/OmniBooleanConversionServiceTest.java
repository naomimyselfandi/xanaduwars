package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class OmniBooleanConversionServiceTest {

    @Mock
    private Function function;

    @Mock
    private Runnable runnable;

    @InjectMocks
    private OmniBooleanConversionService fixture;

    @MethodSource
    @ParameterizedTest
    void convert(@Nullable Object source, boolean expected) {
        assertThat(fixture.convert(source, Boolean.class)).isEqualTo(expected);
        assertThat(fixture.convert(source, boolean.class)).isEqualTo(expected);
    }

    @MethodSource
    @ParameterizedTest
    void convertNullValue(Class<?> type, @Nullable Object expected) {
        assertThat(fixture.convert(null, TypeDescriptor.valueOf(type))).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            true && run(),false,1
            true || run(),true,0
            false && run(),false,0
            false || run(),false,1
            """)
    void providesConditionalSyntax(String code, boolean expected, int calls) {
        var context = new StandardEvaluationContext(runnable);
        context.setTypeConverter(new StandardTypeConverter(fixture));
        var expression = new SpelExpressionParser().parseExpression(code);
        assertThat(expression.getValue(context)).isEqualTo(expected);
        verify(runnable, times(calls)).run();
    }

    private static Stream<Arguments> convert() {
        var falseValues = List.of(
                Optional.empty(),
                OptionalInt.empty(),
                OptionalInt.of(0),
                OptionalLong.empty(),
                OptionalLong.of(0L),
                OptionalDouble.empty(),
                OptionalDouble.of(0.0),
                0,
                0L,
                0.0,
                0.0F,
                Float.NaN,
                Double.NaN,
                "",
                "false",
                List.of(),
                Set.of(),
                Map.of(),
                false
        );
        var trueValues = List.of(
                new Object(),
                OptionalInt.of(42),
                OptionalLong.of(42L),
                OptionalDouble.of(4.2),
                42,
                42L,
                4.2,
                4.2F,
                -42,
                -42L,
                -4.2,
                -4.2F,
                "true",
                "foobar",
                List.of("foo", "bar"),
                Set.of("foo", "bar"),
                Map.of("foo", "bar"),
                true
        );
        return Stream.concat(
                falseValues
                        .stream()
                        .flatMap(it -> Stream.of(it, Optional.of(it)))
                        .map(it -> arguments(it, false)),
                trueValues
                        .stream()
                        .flatMap(it -> Stream.of(it, Optional.of(it)))
                        .map(it -> arguments(it, true))
        );
    }

    private static Stream<Arguments> convertNullValue() {
        return Stream.of(
                arguments(Optional.class, Optional.empty()),
                arguments(OptionalInt.class, OptionalInt.empty()),
                arguments(OptionalLong.class, OptionalLong.empty()),
                arguments(OptionalDouble.class, OptionalDouble.empty()),
                arguments(Boolean.class, false),
                arguments(boolean.class, false),
                arguments(Integer.class, null)
        );
    }

}
