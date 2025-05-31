package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.gameplay.Element;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Name;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ActionWithoutTargetTest {

    private final ActionWithoutTarget<Element> fixture = new ActionWithoutTarget<>() {

        @Override
        public Name name() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Execution execute(Element user, None target) {
            throw new UnsupportedOperationException();
        }

    };

    @Test
    void enumerateTargets() {
        assertThat(fixture.enumerateTargets(mock())).containsExactly(None.NONE);
    }

}
