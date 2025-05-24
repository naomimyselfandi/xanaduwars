package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class VersionNumberTest {

    @Test
    void compareTo() {
        TestUtils.assertSortOrder(
                new VersionNumber("0.0.0"),
                new VersionNumber("0.0.0-Bar"),
                new VersionNumber("0.0.0-Foo"),
                new VersionNumber("0.0.1"),
                new VersionNumber("0.0.1-Bar"),
                new VersionNumber("0.0.1-Foo"),
                new VersionNumber("0.1.0"),
                new VersionNumber("0.1.0-Bar"),
                new VersionNumber("0.1.0-Foo"),
                new VersionNumber("0.1.1"),
                new VersionNumber("0.1.1-Bar"),
                new VersionNumber("0.1.1-Foo"),
                new VersionNumber("1.0.0"),
                new VersionNumber("1.0.0-Bar"),
                new VersionNumber("1.0.0-Foo"),
                new VersionNumber("1.1.0"),
                new VersionNumber("1.1.0-Bar"),
                new VersionNumber("1.1.0-Foo"),
                new VersionNumber("1.1.1"),
                new VersionNumber("1.1.1-Bar"),
                new VersionNumber("1.1.1-Foo"),
                new VersionNumber("1.2.2"),
                new VersionNumber("1.2.2-Bar"),
                new VersionNumber("1.2.2-Foo"),
                new VersionNumber("1.2.3"),
                new VersionNumber("1.2.3-Bar"),
                new VersionNumber("1.2.3-Foo"),
                // We don't really care how malformed versions are sorted,
                // but it should still be well-defined for clear errors.
                new VersionNumber("Bar"),
                new VersionNumber("Foo")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.2.3", "4.5.6-Foobar"})
    void json(String text) {
        TestUtils.assertJson(new VersionNumber(text), "\"%s\"".formatted(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.2.3", "4.5.6-Foobar"})
    void testToString(String text) {
        assertThat(new VersionNumber(text)).hasToString(text);
    }

}
