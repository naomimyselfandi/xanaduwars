package io.github.naomimyselfandi.xanaduwars.gameplay.query;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QueryTest {

    private final Query<Object> query = Object::new;

    @Test
    void shouldShortCircuit() {
        assertThat(query.shouldShortCircuit(new Object())).isFalse();
    }

}
