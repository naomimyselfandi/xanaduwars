package io.github.naomimyselfandi.xanaduwars.core.gamestate.entity;

import io.github.naomimyselfandi.xanaduwars.core.common.SpellId;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ChosenSpellsTest {

    @Test
    void spellIds() {
        var chosenSpells = new ChosenSpells();
        assertThat(chosenSpells.getSpellIds()).isEmpty();
        var spellIds = List.of(new SpellId(1), new SpellId(3));
        assertThat(chosenSpells.setSpellIds(spellIds)).isSameAs(chosenSpells);
        assertThat(chosenSpells.getSpellIds()).isEqualTo(spellIds);
    }

    @Test
    void spellIds_NullHandling() {
        var chosenSpells = new ChosenSpells();
        chosenSpells.setSpellIds(Arrays.asList(new SpellId(4), null, new SpellId(2)));
        assertThat(chosenSpells.getSpellIds()).containsExactly(new SpellId(4), null, new SpellId(2));
    }

}
