package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.TypeConverter;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class OrdinalCreatorTest {

    @Mock
    private TypeConverter delegate;

    @InjectMocks
    private OrdinalCreator fixture;

    @ParameterizedTest
    @CsvSource(textBlock = """
            ,java.lang.Integer,true
            java.lang.Integer,java.lang.Integer,true
            io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator.TestOrdinal,java.lang.Integer,true
            java.lang.Integer,io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator.TestOrdinal,false
            """)
    void canConvert(@Nullable Class<?> sourceClass, Class<?> targetClass, boolean shouldCheckDelegate) {
        var sourceType = sourceClass == null ? null : TypeDescriptor.valueOf(sourceClass);
        var targetType = TypeDescriptor.valueOf(targetClass);
        if (shouldCheckDelegate) {
            for (var result : List.of(true, false)) {
                when(delegate.canConvert(sourceType, targetType)).thenReturn(result);
                assertThat(fixture.canConvert(sourceType, targetType)).isEqualTo(result);
            }
        } else {
            assertThat(fixture.canConvert(sourceType, targetType)).isTrue();
        }
    }

    @Test
    void convertValue_WhenTheSourceIsAnIntegerAndTheTargetIsAnOrdinal_ThenConverts(SeededRng random) {
        var value = random.<TestOrdinal>get();
        var sourceType = TypeDescriptor.valueOf(Integer.class);
        var targetType = TypeDescriptor.valueOf(TestOrdinal.class);
        assertThat(fixture.convertValue(value.ordinal(), null, targetType)).isEqualTo(value);
        assertThat(fixture.convertValue(value.ordinal(), sourceType, targetType)).isEqualTo(value);
    }

    @Test
    void convertValue_WhenTheSourceIsNotAnInteger_ThenDelegates(SeededRng random) {
        var source = random.nextString();
        var sourceType = TypeDescriptor.valueOf(String.class);
        var targetType = TypeDescriptor.valueOf(TestOrdinal.class);
        var value = random.<TestOrdinal>get();
        var valueWithSource = random.<TestOrdinal>get();
        when(delegate.convertValue(source, null, targetType)).thenReturn(value);
        when(delegate.convertValue(source, sourceType, targetType)).thenReturn(valueWithSource);
        assertThat(fixture.convertValue(source, null, targetType)).isEqualTo(value);
        assertThat(fixture.convertValue(source, sourceType, targetType)).isEqualTo(valueWithSource);
    }

    @Test
    void convertValue_WhenTheTargetIsNotAnOrdinal_ThenDelegates(SeededRng random) {
        var source = random.nextInt();
        var sourceType = TypeDescriptor.valueOf(Integer.class);
        var targetType = TypeDescriptor.valueOf(Integer.class);
        var value = random.nextString();
        var valueWithSource = random.nextString();
        when(delegate.convertValue(source, null, targetType)).thenReturn(value);
        when(delegate.convertValue(source, sourceType, targetType)).thenReturn(valueWithSource);
        assertThat(fixture.convertValue(source, null, targetType)).isEqualTo(value);
        assertThat(fixture.convertValue(source, sourceType, targetType)).isEqualTo(valueWithSource);
    }

}
