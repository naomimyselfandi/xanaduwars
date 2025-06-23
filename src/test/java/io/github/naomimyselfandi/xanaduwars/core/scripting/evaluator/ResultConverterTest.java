package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.TypeConverter;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ResultConverterTest {

    @Mock
    private TypeConverter delegate;

    @InjectMocks
    private ResultConverter fixture;

    @MethodSource
    @ParameterizedTest
    void canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType, boolean shouldDelegate) {
        if (shouldDelegate) {
            for (var value : List.of(true, false)) {
                when(delegate.canConvert(sourceType, targetType)).thenReturn(value);
                assertThat(fixture.canConvert(sourceType, targetType)).isEqualTo(value);
            }
        } else {
            assertThat(fixture.canConvert(sourceType, targetType)).isTrue();
        }
    }

    @MethodSource
    @ParameterizedTest
    void convertValue(
            @Nullable Object value,
            @Nullable TypeDescriptor sourceType,
            TypeDescriptor targetType,
            @Nullable Object expected
    ) {
        if (expected == null) {
            expected = new Object();
            when(delegate.convertValue(value, sourceType, targetType)).thenReturn(expected);
        }
        assertThat(fixture.convertValue(value, sourceType, targetType)).isEqualTo(expected);
    }

    private static Stream<Arguments> canConvert() {
        TypeDescriptor string = TypeDescriptor.valueOf(String.class);
        TypeDescriptor integer = TypeDescriptor.valueOf(Integer.class);
        TypeDescriptor result = TypeDescriptor.valueOf(Result.class);
        return Stream.of(
                arguments(null, result, false),
                arguments(string, result, false),
                arguments(integer, result, false),
                arguments(integer, string, true),
                arguments(integer, string, true),
                arguments(result, TypeDescriptor.valueOf(Boolean.class), false),
                arguments(result, TypeDescriptor.valueOf(boolean.class), false),
                arguments(integer, TypeDescriptor.valueOf(Boolean.class), true),
                arguments(integer, TypeDescriptor.valueOf(boolean.class), true)
        );
    }

    private static Stream<Arguments> convertValue() {
        TypeDescriptor string = TypeDescriptor.valueOf(String.class);
        TypeDescriptor integer = TypeDescriptor.valueOf(Integer.class);
        TypeDescriptor result = TypeDescriptor.valueOf(Result.class);
        return Stream.of(
                arguments("foo", string, result, Result.fail("foo")),
                arguments(Result.okay(), result, TypeDescriptor.valueOf(Boolean.class), true),
                arguments(Result.okay(), result, TypeDescriptor.valueOf(boolean.class), true),
                arguments(Result.fail("bar"), result, TypeDescriptor.valueOf(Boolean.class), false),
                arguments(Result.fail("bar"), result, TypeDescriptor.valueOf(boolean.class), false),
                arguments(42, integer, string, null),
                arguments(Result.okay(), result, result, null)
        );
    }

}
