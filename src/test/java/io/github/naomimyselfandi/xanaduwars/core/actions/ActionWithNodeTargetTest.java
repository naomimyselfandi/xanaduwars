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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionWithNodeTargetTest {

    @Mock
    private GameState gameState;

    @Mock
    private Unit unit0, unit1;

    @Mock
    private Tile tile0, tile1, tile2, tile3;

    @BeforeEach
    void setup() {
        when(gameState.units()).thenReturn(List.of(unit0, unit1));
        when(gameState.tiles()).thenReturn(List.of(List.of(tile0, tile1), List.of(tile2, tile3)));
    }

    private final ActionWithNodeTarget<Element> fixture = new ActionWithNodeTarget<>() {

        @Override
        public Name name() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TagSet tags() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Execution execute(Element user, Node target) {
            throw new UnsupportedOperationException();
        }

    };

    @Test
    void enumerateTargets() {
        assertThat(fixture.enumerateTargets(gameState)).containsExactly(unit0, unit1, tile0, tile1, tile2, tile3);
    }

}
