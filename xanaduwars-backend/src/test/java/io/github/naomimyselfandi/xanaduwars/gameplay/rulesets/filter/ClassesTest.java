package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ClassesTest {

    @Test
    void test() {
        assertThat(Classes.ELEMENT_TYPES_BY_NAME)
                .hasSize(7)
                .containsEntry("Element", Element.class)
                .containsEntry("Actor", Actor.class)
                .containsEntry("Player", Player.class)
                .containsEntry("Spell", Spell.class)
                .containsEntry("Node", Node.class)
                .containsEntry("Tile", Tile.class)
                .containsEntry("Unit", Unit.class);
    }

}
