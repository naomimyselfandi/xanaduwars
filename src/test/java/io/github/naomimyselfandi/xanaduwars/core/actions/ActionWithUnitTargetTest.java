package io.github.naomimyselfandi.xanaduwars.core.actions;

import io.github.naomimyselfandi.xanaduwars.core.*;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActionWithUnitTargetTest {

    @Mock
    private GameState gameState;

    @Mock
    private Unit unit0, unit1;

    @BeforeEach
    void setup() {
        when(gameState.units()).thenReturn(List.of(unit0, unit1));
    }

    private final ActionWithUnitTarget<Element> fixture = new ActionWithUnitTarget<>() {

        @Override
        public Name name() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TagSet tags() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Execution execute(Element user, Unit target) {
            throw new UnsupportedOperationException();
        }

    };

    @Test
    void enumerateTargets() {
        assertThat(fixture.enumerateTargets(gameState)).containsExactly(unit0, unit1);
    }

}
