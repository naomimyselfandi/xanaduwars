package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import org.jetbrains.annotations.Unmodifiable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class QueryWithTargetsTest {

    private final List<Object> targets = new ArrayList<>();

    private final QueryWithTargets<Object> fixture = new QueryWithTargets<>() {

        @Override
        public @Unmodifiable List<?> targets() {
            return List.copyOf(targets);
        }

        @Override
        public Object defaultValue() {
            throw new UnsupportedOperationException();
        }

    };

    @Test
    void target() {
        var target = new Object();
        targets.add(target);
        targets.add(new Object());
        assertThat(fixture.target()).isEqualTo(target);
    }

    @Test
    void target_WhenThereAreNoTargets_ThenNull() {
        assertThat(fixture.target()).isNull();
    }

}
