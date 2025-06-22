package io.github.naomimyselfandi.xanaduwars.util;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import lombok.With;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class OrdinalTest {

    private record Helper(@With int ordinal) implements Ordinal<Helper> {}

    @ParameterizedTest
    @CsvSource(textBlock = """
            1,2,3
            40,2,42
            """)
    void plus(int lhs, int rhs, int expected) {
        assertThat(new Helper(lhs).plus(rhs)).isEqualTo(new Helper(expected));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            3,2,1
            50,8,42
            """)
    void minus(int lhs, int rhs, int expected) {
        assertThat(new Helper(lhs).minus(rhs)).isEqualTo(new Helper(expected));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            2,3,6
            6,7,42
            """)
    void times(int lhs, int rhs, int expected) {
        assertThat(new Helper(lhs).times(rhs)).isEqualTo(new Helper(expected));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            6,3,2
            7,3,2
            84,2,42
            """)
    void dividedBy(int lhs, int rhs, int expected) {
        assertThat(new Helper(lhs).dividedBy(rhs)).isEqualTo(new Helper(expected));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,3,0
            1,3,1
            2,3,2
            3,3,0
            4,3,1
            142,100,42
            """)
    void modulo(int lhs, int rhs, int expected) {
        assertThat(new Helper(lhs).modulo(rhs)).isEqualTo(new Helper(expected));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            42,43,-1
            42,42,0
            43,42,1
            """)
    void compareTo(int lhs, int rhs, int expected) {
        assertThat(new Helper(lhs).compareTo(new Helper(rhs))).isEqualTo(expected);
    }

}
