package io.github.naomimyselfandi.xanaduwars.gameplay.common;

import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

class RangeTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0,true
            1,1,true
            0,1,true
            0,2,true
            1,2,true
            1,0,false
            2,0,false
            2,1,false
            """)
    void isConsistent(int min, int max, boolean expected) {
        assertThat(new Range(min, max).isConsistent()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            1,1,0,false
            1,1,1,true
            1,1,2,false
            0,2,1,true
            0,2,2,true
            1,2,1,true
            1,2,2,true
            1,2,0,false
            1,2,3,false
            """)
    void test(int min, int max, int value, boolean expected) {
        assertThat(new Range(min, max).test(value)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,0,0-0
            0,1,0-1
            1,2,1-2
            """)
    void testToString(int min, int max, String expected) {
        assertThat(new Range(min, max)).hasToString(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0 1 {"min":0,"max":1}
            1 2 {"min":1,"max":2}
            """, delimiter = ' ')
    void json(int min, int max, @Language("json") String json) {
        TestUtils.assertJson(new Range(min, max), json);
    }

}
