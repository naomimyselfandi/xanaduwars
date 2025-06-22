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
class ResultCreatorTest {

    @Mock
    private TypeConverter delegate;

    @InjectMocks
    private ResultCreator fixture;

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
        TypeDescriptor fail = TypeDescriptor.valueOf(Result.Fail.class);
        return Stream.of(
                arguments(null, result, false),
                arguments(null, fail, false),
                arguments(string, result, false),
                arguments(string, fail, false),
                arguments(integer, result, false),
                arguments(integer, fail, false),
                arguments(integer, string, true),
                arguments(integer, string, true)
        );
    }

    private static Stream<Arguments> convertValue() {
        TypeDescriptor string = TypeDescriptor.valueOf(String.class);
        TypeDescriptor integer = TypeDescriptor.valueOf(Integer.class);
        TypeDescriptor result = TypeDescriptor.valueOf(Result.class);
        TypeDescriptor fail = TypeDescriptor.valueOf(Result.Fail.class);
        return Stream.of(
                arguments("foo", string, result, Result.fail("foo")),
                arguments("bar", string, fail, Result.fail("bar")),
                arguments(42, integer, string, null),
                arguments(Result.okay(), result, result, null),
                arguments(Result.fail("baz"), result, result, null),
                arguments(Result.fail("baz"), result, fail, null)
        );
    }

}
