package io.github.naomimyselfandi.xanaduwars.util;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class ImmutableListTest {

    private String foo, bar;

    private ImmutableList<String> fixture;

    @BeforeEach
    void setup(SeededRng random) {
        foo = random.nextString();
        bar = random.nextString();
        fixture = new ImmutableList<>(new ArrayList<>(List.of(foo, bar)));
    }

    @Test
    void isImmutable() {
        assertThat(fixture).isUnmodifiable();
        assertThat(fixture.source()).isSameAs(List.copyOf(fixture.source()));
    }

    @Test
    void testEquals() {
        assertThat(fixture).isEqualTo(List.of(foo, bar));
        assertThat(fixture).isNotEqualTo(List.of(bar, foo));
    }

    @Test
    void testHashCode() {
        assertThat(fixture).hasSameHashCodeAs(List.of(foo, bar));
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString(List.of(foo, bar).toString());
    }

    @Test
    void iterator() {
        assertThat(fixture).containsExactly(foo, bar);
    }

}
