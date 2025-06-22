package io.github.naomimyselfandi.xanaduwars.core.scripting;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class QueryTest {

    private final Query<Object> fixture = () -> {
        throw new UnsupportedOperationException();
    };

    @Test
    void subject() {
        assertThat(fixture.subject()).isNull();
    }

    @Test
    void prologue() {
        assertThat(fixture.prologue()).isEmpty();
    }

    @Test
    void epilogue() {
        assertThat(fixture.epilogue()).isEmpty();
    }

}
