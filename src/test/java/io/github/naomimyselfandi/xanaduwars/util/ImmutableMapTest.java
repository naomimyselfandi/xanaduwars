package io.github.naomimyselfandi.xanaduwars.util;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class ImmutableMapTest {

    private String foo, bar;

    private ImmutableMap<String, String> fixture;

    @BeforeEach
    void setup(SeededRng random) {
        foo = random.nextString();
        bar = random.nextString();
        fixture = new ImmutableMap<>(new HashMap<>(Map.of(foo, bar)));
    }

    @Test
    void isImmutable() {
        assertThat(fixture).isUnmodifiable();
        assertThat(fixture.source()).isSameAs(Map.copyOf(fixture.source()));
    }

    @Test
    void testEquals() {
        assertThat(fixture).isEqualTo(Map.of(foo, bar));
        assertThat(fixture).isNotEqualTo(Map.of(bar, foo));
    }

    @Test
    void testHashCode() {
        assertThat(fixture).hasSameHashCodeAs(Map.of(foo, bar));
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString(Map.of(foo, bar).toString());
    }

    @Test
    void entrySet() {
        assertThat(fixture.entrySet()).containsExactly(Map.entry(foo, bar));
    }

}
