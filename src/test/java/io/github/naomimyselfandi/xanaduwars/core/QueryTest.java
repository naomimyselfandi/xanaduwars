package io.github.naomimyselfandi.xanaduwars.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QueryTest {

    @Test
    void shouldShortCircuit() {
        assertThat(new Query<>() {}.shouldShortCircuit(new Object())).isFalse();
    }

}
