package io.github.naomimyselfandi.xanaduwars.core.actions;

import io.github.naomimyselfandi.xanaduwars.core.Direction;
import io.github.naomimyselfandi.xanaduwars.core.Execution;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActionWithEnumTargetTest {

    private final ActionWithEnumTarget<Unit, Direction> fixture = new ActionWithEnumTarget<>() {

        @Override
        public Name name() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TagSet tags() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Execution execute(Unit user, Direction target) {
            throw new UnsupportedOperationException();
        }

    };

    @Test
    void enumerateTargets() {
        assertThat(fixture.enumerateTargets(mock())).containsExactly(Direction.values());
    }

}
