package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class OrdinalConverterTest {

    @Mock
    private TypeConverter delegate;

    @InjectMocks
    private OrdinalConverter fixture;

    @ParameterizedTest
    @CsvSource(textBlock = """
            ,java.lang.Integer,true
            java.lang.String,java.lang.Integer,true
            io.github.naomimyselfandi.xanaduwars.util.Ordinal,java.lang.Integer,false
            io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator.TestOrdinal,java.lang.Integer,false
            java.lang.Integer,io.github.naomimyselfandi.xanaduwars.util.Ordinal,true
            java.lang.Integer,io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator.TestOrdinal,true
            """)
    void canConvert(@Nullable Class<?> sourceClass, Class<?> targetClass, boolean shouldDelegate) {
        var sourceType = sourceClass == null ? null : TypeDescriptor.valueOf(sourceClass);
        var targetType = TypeDescriptor.valueOf(targetClass);
        if (shouldDelegate) {
            for (var value : List.of(true, false)) {
                when(delegate.canConvert(sourceType, targetType)).thenReturn(value);
                assertThat(fixture.canConvert(sourceType, targetType)).isEqualTo(value);
            }
        } else {
            assertThat(fixture.canConvert(sourceType, targetType)).isTrue();
        }
    }

    @Test
    void convertValue_WhenTheSourceIsAnOrdinalAndTheTargetTypeIsTheIntegerType_ThenConverts(SeededRng random) {
        var source = random.<TestOrdinal>get();
        var targetType = TypeDescriptor.valueOf(Integer.class);
        var sourceType = TypeDescriptor.forObject(source);
        assertThat(fixture.convertValue(source, null, targetType)).isEqualTo(source.ordinal());
        assertThat(fixture.convertValue(source, sourceType, targetType)).isEqualTo(source.ordinal());
    }

    @Test
    void convertValue_WhenTheSourceIsAnOrdinalButTheTargetTypeIsNotTheIntegerType_ThenDelegates(SeededRng random) {
        var source = random.<TestOrdinal>get();
        var targetType = TypeDescriptor.valueOf(String.class);
        var sourceType = TypeDescriptor.forObject(source);
        var value = random.nextString();
        var valueWithSourceType = random.nextString();
        when(delegate.convertValue(source, null, targetType)).thenReturn(value);
        when(delegate.convertValue(source, sourceType, targetType)).thenReturn(valueWithSourceType);
        assertThat(fixture.convertValue(source, null, targetType)).isEqualTo(value);
        assertThat(fixture.convertValue(source, sourceType, targetType)).isEqualTo(valueWithSourceType);
    }

    @ParameterizedTest
    @ValueSource(classes = {Integer.class, String.class})
    void convertValue_WhenTheSourceIsNotAnOrdinal_ThenDelegates(Class<?> targetClass) {
        var source = new Object();
        var targetType = TypeDescriptor.valueOf(targetClass);
        var sourceType = TypeDescriptor.forObject(source);
        var value = new Object();
        var valueWithSourceType = new Object();
        when(delegate.convertValue(source, null, targetType)).thenReturn(value);
        when(delegate.convertValue(source, sourceType, targetType)).thenReturn(valueWithSourceType);
        assertThat(fixture.convertValue(source, null, targetType)).isEqualTo(value);
        assertThat(fixture.convertValue(source, sourceType, targetType)).isEqualTo(valueWithSourceType);
    }

}
