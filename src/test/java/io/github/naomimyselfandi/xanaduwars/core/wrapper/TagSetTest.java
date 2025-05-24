package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class TagSetTest {

    private final Tag foo = new Tag("Foo"), bar = new Tag("Bar"), baz = new Tag("Baz");

    private final TagSet fixture = new TagSet(new HashSet<>(List.of(foo, bar)));

    @Test
    void test() {
        assertThat(fixture.test(foo)).isTrue();
        assertThat(fixture.test(bar)).isTrue();
        assertThat(fixture.test(baz)).isFalse();
    }

    @Test
    void union() {
        assertThatCollection(fixture.union(List.of(bar, baz))).containsOnly(foo, bar, baz);
    }

    @Test
    void intersection() {
        assertThatCollection(fixture.intersection(List.of(bar, baz))).containsOnly(bar);
    }

    @Test
    void with() {
        assertThatCollection(fixture.with(bar, baz)).containsOnly(foo, bar, baz);
    }

    @Test
    void testEquals() {
        assertThatCollection(fixture).isEqualTo(Set.of(foo, bar));
        assertThatCollection(fixture).isNotEqualTo(Set.of(foo, baz));
    }

    @Test
    void testHashCode() {
        assertThatCollection(fixture).hasSameHashCodeAs(Set.of(foo, bar));
    }

    @Test
    void testToString() {
        assertThatCollection(fixture).hasToString("[%s, %s]", bar, foo);
    }

    @Test
    void tags() {
        assertThat(fixture.tags()).isEqualTo(Set.of(foo, bar));
    }

    @Test
    void size() {
        assertThatCollection(fixture).hasSize(2);
    }

    @Test
    void json() {
        TestUtils.assertJson(fixture, "[\"Foo\", \"Bar\"]");
    }

}
