package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class TagTest {

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "foo", "Foo bar", " Foo", "Bar ", "Foo!"})
    void prohibitsInvalidStrings(String string) {
        assertThatThrownBy(() -> new Tag(string)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Foo", "Bar", "F", "FooBar"})
    void acceptsValidStrings(String string) {
        assertThatCode(() -> new Tag(string)).doesNotThrowAnyException();
    }

    @Test
    void compareTo() {
        TestUtils.assertSortOrder(new Tag("Bar"), new Tag("Foo"), new Tag("Foobar"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Foo", "Bar"})
    void testToString(String string) {
        assertThat(new Tag(string)).hasToString(string);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Foo", "Bar"})
    void json(String string) {
        TestUtils.assertJson(new Tag(string), "\"%s\"".formatted(string));
    }

}
