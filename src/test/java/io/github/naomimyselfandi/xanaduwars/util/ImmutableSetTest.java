package io.github.naomimyselfandi.xanaduwars.util;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class ImmutableSetTest {

    private String foo, bar, baz;

    private ImmutableSet<String> fixture;

    @BeforeEach
    void setup(SeededRng random) {
        foo = random.nextString();
        bar = random.nextString();
        baz = random.nextString();
        fixture = new ImmutableSet<>(new HashSet<>(Set.of(foo, bar)));
    }

    @Test
    void isImmutable() {
        assertThat(fixture).isUnmodifiable();
        assertThat(fixture.source()).isSameAs(Set.copyOf(fixture.source()));
    }

    @Test
    void testEquals() {
        assertThat(fixture).isEqualTo(Set.of(foo, bar));
        assertThat(fixture).isNotEqualTo(Set.of(foo));
        assertThat(fixture).isNotEqualTo(Set.of(foo, bar, baz));
    }

    @Test
    void testHashCode() {
        assertThat(fixture).hasSameHashCodeAs(Set.of(foo, bar));
    }

    @Test
    void testToString() {
        assertThat(fixture.toString()).isIn(List.of(foo, bar).toString(), List.of(bar, foo).toString());
    }

    @Test
    void iterator() {
        assertThat(fixture).containsOnly(foo, bar);
    }

}
