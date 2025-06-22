package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.SpellId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.PlayerData;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Commander;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Spell;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class SpellSlotHelperImplTest {

    @Mock
    private Spell signatureSpell0, signatureSpell1, chosenSpell0, chosenSpell1;

    private SpellId chosenSpell0Id, chosenSpell1Id;

    @Mock
    private Commander commander;

    @Mock
    private Ruleset ruleset;

    private PlayerData playerData;

    private SpellSlotHelperImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        chosenSpell0Id = random.get();
        when(ruleset.getSpell(chosenSpell0Id)).thenReturn(chosenSpell0);
        chosenSpell1Id = random.not(chosenSpell0Id);
        when(ruleset.getSpell(chosenSpell1Id)).thenReturn(chosenSpell1);
        playerData = random.get();
        when(ruleset.getCommander(Objects.requireNonNull(playerData.getCommanderId()))).thenReturn(commander);
        when(commander.getSignatureSpells()).thenReturn(List.of(signatureSpell0, signatureSpell1));
        fixture = new SpellSlotHelperImpl();
    }

    @Test
    void getSpellSlots() {
        playerData.getChosenSpells().setSpellIds(List.of(chosenSpell0Id, chosenSpell1Id));
        var signatureSpellActivation = playerData.getSignatureSpellActivation();
        var chosenSpellActivation = playerData.getChosenSpellActivation();
        var chosenSpellRevelation = playerData.getChosenSpellRevelation();
        assertThat(fixture.getSpellSlots(ruleset, playerData)).containsExactly(
                new SignatureSpellSlot(signatureSpell0, signatureSpellActivation, 0),
                new SignatureSpellSlot(signatureSpell1, signatureSpellActivation, 1),
                new ChosenSpellSlot(chosenSpell0, chosenSpellActivation, chosenSpellRevelation, 0),
                new ChosenSpellSlot(chosenSpell1, chosenSpellActivation, chosenSpellRevelation, 1)
        );
    }

    @Test
    void getSpellSlots_ToleratesNull() {
        playerData.getChosenSpells().setSpellIds(Arrays.asList(chosenSpell0Id, null, chosenSpell1Id));
        var signatureSpellActivation = playerData.getSignatureSpellActivation();
        var chosenSpellActivation = playerData.getChosenSpellActivation();
        var chosenSpellRevelation = playerData.getChosenSpellRevelation();
        assertThat(fixture.getSpellSlots(ruleset, playerData)).containsExactly(
                new SignatureSpellSlot(signatureSpell0, signatureSpellActivation, 0),
                new SignatureSpellSlot(signatureSpell1, signatureSpellActivation, 1),
                new ChosenSpellSlot(chosenSpell0, chosenSpellActivation, chosenSpellRevelation, 0),
                new ChosenSpellSlot(null, chosenSpellActivation, chosenSpellRevelation, 1),
                new ChosenSpellSlot(chosenSpell1, chosenSpellActivation, chosenSpellRevelation, 2)
        );
    }

}
