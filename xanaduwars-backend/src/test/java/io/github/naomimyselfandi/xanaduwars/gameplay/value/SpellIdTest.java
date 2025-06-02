package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class SpellIdTest {

    @RepeatedTest(2)
    void testToString(SeededRng random) {
        var spellId = random.nextSpellId();
        assertThat(spellId).hasToString("Spell(%d, %d)", spellId.ownerId().intValue(), spellId.index());
    }

}
