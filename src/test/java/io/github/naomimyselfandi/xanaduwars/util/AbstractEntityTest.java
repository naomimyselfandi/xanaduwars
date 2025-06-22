package io.github.naomimyselfandi.xanaduwars.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class AbstractEntityTest {

    private static class Fixture extends AbstractEntity<Fixture> {}

    private Fixture fixture;

    @BeforeEach
    void setup() {
        fixture = new Fixture();
    }

    @Test
    void getId() {
        var id = fixture.getId();
        assertThat(id).isNotNull().isEqualTo(fixture.getId());
    }

    @Test
    void getId_IsUnique() {
        var repetitions = 10000;
        var ids = new HashSet<Id<Fixture>>(repetitions);
        for (var i = 0; i < repetitions; i++) {
            assertThat(ids.add(new Fixture().getId())).isTrue();
        }
    }

    @Test
    void setId() {
        var id = new Id<Fixture>(UUID.randomUUID());
        assertThat(fixture.setId(id)).isSameAs(fixture);
        assertThat(fixture.getId()).isEqualTo(id);
    }

    @Test
    @SuppressWarnings("AssertBetweenInconvertibleTypes")
    void testEquals() {
        assertThat(fixture).isNotEqualTo(null);
        assertThat(fixture).isNotEqualTo(fixture.getId());
        var that = new Fixture();
        assertThat(fixture).isNotEqualTo(that);
        that.setId(fixture.getId());
        assertThat(fixture).isEqualTo(that);
    }

    @Test
    void testHashCode() {
        assertThat(fixture).hasSameHashCodeAs(fixture.getId());
    }

    @Test
    void testToString() {
        assertThat(fixture).hasToString("Fixture[id=%s]", fixture.getId().id());
    }

}
