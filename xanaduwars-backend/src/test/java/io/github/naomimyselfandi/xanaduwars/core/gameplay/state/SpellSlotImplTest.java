package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.SpellSlotData;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Spell;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.SpellId;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class SpellSlotImplTest {

    @Mock
    private Spell foo, bar, baz;

    @Mock
    private Ruleset ruleset;

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void spell(int index, SeededRng random) {
        var spells = List.of(foo, bar, baz);
        when(ruleset.spells()).thenReturn(spells);
        var spellId = new SpellId(index);
        var revealed = random.nextBoolean();
        var timesCastThisTurn = random.nextIntNotNegative();
        var data = new SpellSlotData(spellId, revealed, timesCastThisTurn);
        var slot = new SpellSlotImpl(data, ruleset);
        assertThat(slot.spell()).isEqualTo(spells.get(index));
        assertThat(slot).hasToString("SpellSlot[spell=%s]", spells.get(index));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void revealed(boolean revealed, SeededRng random) {
        var spellId = new SpellId(random.nextInt(0, 4));
        var timesCastThisTurn = random.nextIntNotNegative();
        var data = new SpellSlotData(spellId, revealed, timesCastThisTurn);
        var slot = new SpellSlotImpl(data, ruleset);
        assertThat(slot.revealed()).isEqualTo(revealed);
    }

    @RepeatedTest(2)
    void timesCastThisTurn(SeededRng random) {
        var spellId = new SpellId(random.nextInt(0, 4));
        var revealed = random.nextBoolean();
        var timesCastThisTurn = random.nextIntNotNegative();
        var data = new SpellSlotData(spellId, revealed, timesCastThisTurn);
        var slot = new SpellSlotImpl(data, ruleset);
        assertThat(slot.timesCastThisTurn()).isEqualTo(timesCastThisTurn);
    }

}
