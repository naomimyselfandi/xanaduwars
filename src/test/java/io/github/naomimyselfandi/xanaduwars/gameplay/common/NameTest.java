package io.github.naomimyselfandi.xanaduwars.gameplay.common;

import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class NameTest {

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "foo", "Foo bar", " Foo", "Bar ", "Foo!"})
    void prohibitsInvalidStrings(String string) {
        assertThatThrownBy(() -> new Name(string)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Foo", "Bar", "F", "FooBar"})
    void acceptsValidStrings(String string) {
        assertThatCode(() -> new Name(string)).doesNotThrowAnyException();
    }

    @Test
    void compareTo() {
        TestUtils.assertSortOrder(new Name("Bar"), new Name("Foo"), new Name("Foobar"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Foo", "Bar"})
    void testToString(String string) {
        assertThat(new Name(string)).hasToString(string);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Foo", "Bar"})
    void json(String string) {
        TestUtils.assertJson(new Name(string), "\"%s\"".formatted(string));
    }

}
