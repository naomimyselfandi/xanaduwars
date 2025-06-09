package io.github.naomimyselfandi.xanaduwars.core.scripting;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class QueryTest {

    private final Query<Object> fixture = new Query<>() {

        @Override
        public @Nullable Object subject() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object defaultValue() {
            throw new UnsupportedOperationException();
        }

    };

    @Test
    void prologue() {
        assertThat(fixture.prologue()).isEmpty();
    }

    @Test
    void epilogue() {
        assertThat(fixture.epilogue()).isEmpty();
    }

}
