package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.Direction;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Name;
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
        public Execution execute(Unit user, Direction target) {
            throw new UnsupportedOperationException();
        }

    };

    @Test
    void enumerateTargets() {
        assertThat(fixture.enumerateTargets(mock())).containsExactly(Direction.values());
    }

}
