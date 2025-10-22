package io.github.naomimyselfandi.xanaduwars.util;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class AbstractEntityTest {

    private static class Fixture extends AbstractEntity<Fixture> {}

    private final Fixture fixture = new Fixture();

    @Test
    void getId() {
        var id = fixture.getId();
        assertThat(fixture.getId()).isEqualTo(id);
    }

    @Test
    void setId(SeededRng random) {
        var id = random.<Id<Fixture>>get();
        assertThat(fixture.setId(id)).isSameAs(fixture);
        assertThat(fixture.getId()).isEqualTo(id);
    }

    @Test
    @SuppressWarnings({"EqualsWithItself", "AssertBetweenInconvertibleTypes"})
    void testEquals() {
        assertThat(fixture).isNotEqualTo(null);
        assertThat(fixture).isEqualTo(fixture);
        assertThat(fixture).isNotEqualTo(fixture.getId());
        var other = new Fixture();
        assertThat(fixture).isNotEqualTo(other);
        other.setId(fixture.getId());
        assertThat(fixture).isEqualTo(other);
    }

    @Test
    void testHashCode() {
        assertThat(fixture).hasSameHashCodeAs(fixture.getId());
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("Fixture[id=%s]", fixture.getId());
    }

}
