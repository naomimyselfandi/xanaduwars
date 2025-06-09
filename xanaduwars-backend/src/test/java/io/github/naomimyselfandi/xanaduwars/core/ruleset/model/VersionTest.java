package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class VersionTest {

    @Test
    void compareTo() {
        TestUtils.assertSortOrder(
                new Version("0.0.0"),
                new Version("0.0.0-Bar"),
                new Version("0.0.0-Foo"),
                new Version("0.0.1"),
                new Version("0.0.1-Bar"),
                new Version("0.0.1-Foo"),
                new Version("0.1.0"),
                new Version("0.1.0-Bar"),
                new Version("0.1.0-Foo"),
                new Version("0.1.1"),
                new Version("0.1.1-Bar"),
                new Version("0.1.1-Foo"),
                new Version("1.0.0"),
                new Version("1.0.0-Bar"),
                new Version("1.0.0-Foo"),
                new Version("1.1.0"),
                new Version("1.1.0-Bar"),
                new Version("1.1.0-Foo"),
                new Version("1.1.1"),
                new Version("1.1.1-Bar"),
                new Version("1.1.1-Foo"),
                new Version("1.2.2"),
                new Version("1.2.2-Bar"),
                new Version("1.2.2-Foo"),
                new Version("1.2.3"),
                new Version("1.2.3-Bar"),
                new Version("1.2.3-Foo"),
                // We don't really care how malformed versions are sorted,
                // but it should still be well-defined for clear errors.
                new Version("Bar"),
                new Version("Foo")
        );
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            1.2.3,true
            1.2.3-Foo,false
            4.5.6,true
            4.5.6-Bar,false
            """)
    void isPublished(String text, boolean expected) {
        assertThat(new Version(text).isPublished()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.2.3", "4.5.6-Foobar"})
    void json(String text) {
        TestUtils.assertJson(new Version(text), "\"%s\"".formatted(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.2.3", "4.5.6-Foobar"})
    void testToString(String text) {
        assertThat(new Version(text)).hasToString(text);
    }

}
