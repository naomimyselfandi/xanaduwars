package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class LibraryAccessorTest {

    @Mock
    private EvaluationContext context;

    @Mock
    private Library library;

    private LibraryAccessor fixture;

    @BeforeEach
    void setup() {
        fixture = new LibraryAccessor();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            something,true
            ,false
            """)
    void canRead(@Nullable Object value, boolean expected, SeededRng random) {
        var name = random.nextString();
        when(library.lookup(name)).thenReturn(value);
        assertThat(fixture.canRead(context, library, name)).isEqualTo(expected);
        verifyNoInteractions(context);
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(strings = "something")
    void canRead_WhenTheTargetIsNotALibrary_ThenFalse(@Nullable Object target, SeededRng random) {
        var name = random.nextString();
        assertThat(fixture.canRead(context, target, name)).isFalse();
        verifyNoInteractions(context);
    }

    @Test
    void read(SeededRng random) {
        var name = random.nextString();
        var value = new Object();
        when(library.lookup(name)).thenReturn(value);
        assertThat(fixture.read(context, library, name)).isEqualTo(new TypedValue(value));
        verifyNoInteractions(context);
    }

    @Test
    void canWrite(SeededRng random) {
        var name = random.nextString();
        assertThat(fixture.canWrite(context, library, name)).isFalse();
        verifyNoInteractions(context, library);
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(strings = "something")
    void write(@Nullable Object value, SeededRng random) {
        var name = random.nextString();
        assertThatThrownBy(() -> fixture.write(context, library, name, value)).isInstanceOf(AccessException.class);
        verifyNoInteractions(context, library);
    }

    @Test
    void getSpecificTargetClasses() {
        assertThat(fixture.getSpecificTargetClasses()).containsExactly(Library.class);
    }

}
