package io.github.naomimyselfandi.xanaduwars.core.model;

import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class VersionNumberTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            1,2,3,'',1.2.3
            4,5,6,foo,4.5.6-foo
            """)
    void of(int major, int minor, int patch, String suffix, String input) {
        assertThat(VersionNumber.of(input)).isEqualTo(new VersionNumber(major, minor, patch, suffix));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "1", "1.", "1.2", "1.2.", "1.2.3.", "1.2.3.4", "1.2.3.foo"})
    void of_WhenTheInputIsInvalid_ThenThrows(String input) {
        assertThatThrownBy(() -> VersionNumber.of(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid version number '%s'.", input);
    }

    @Test
    void compareTo() {
        TestUtils.assertSortOrder(
                new VersionNumber(1, 0, 0, "foo"),
                new VersionNumber(1, 0, 0, "bar"),
                new VersionNumber(1, 0, 0, ""),
                new VersionNumber(1, 0, 1, ""),
                new VersionNumber(1, 1, 0, ""),
                new VersionNumber(1, 1, 1, ""),
                new VersionNumber(2, 0, 0, ""),
                new VersionNumber(2, 0, 1, ""),
                new VersionNumber(2, 1, 0, ""),
                new VersionNumber(2, 1, 1, "")
        );
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            1,2,3,'',1.2.3
            4,5,6,foo,4.5.6-foo
            """)
    void testToString(int major, int minor, int patch, String suffix, String expected) {
        assertThat(new VersionNumber(major, minor, patch, suffix)).hasToString(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            1,2,3,'',"1.2.3"
            4,5,6,foo,"4.5.6-foo"
            """)
    void json(int major, int minor, int patch, String suffix, @Language("json") String json) {
        TestUtils.assertJson(new VersionNumber(major, minor, patch, suffix), json);
    }

}
