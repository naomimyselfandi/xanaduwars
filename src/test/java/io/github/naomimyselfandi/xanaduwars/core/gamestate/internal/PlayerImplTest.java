package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.common.CommanderId;
import io.github.naomimyselfandi.xanaduwars.core.common.SpellId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.*;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.PlayerData;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.VisionCheckQuery;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Commander;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.NormalAction;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Spell;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class PlayerImplTest {

    @Mock
    private NormalAction pass, yield;

    @Mock
    private Player player;

    @Mock
    private Structure structure0, structure1;

    @Mock
    private Unit unit0, unit1;

    @Mock
    private Commander commander;

    @Mock
    private Spell spell;

    @Mock
    private SpellSlot spellSlot;

    private PlayerData playerData;

    @Mock
    private Ruleset ruleset;

    @Mock
    private SpellSlotHelper spellSlotHelper;

    @Mock
    private GameState gameState;

    private PlayerImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        playerData = random.get();
        fixture = new PlayerImpl(playerData, gameState, random.get(), spellSlotHelper);
    }

    @Test
    void getCommander(SeededRng random) {
        var commanderId = random.<CommanderId>get();
        playerData.setCommanderId(commanderId);
        when(gameState.getRuleset()).thenReturn(ruleset);
        when(ruleset.getCommander(commanderId)).thenReturn(commander);
        assertThat(fixture.getCommander()).contains(commander);
    }

    @Test
    void setCommander(SeededRng random) {
        var commanderId = random.<CommanderId>get();
        when(commander.getId()).thenReturn(commanderId);
        assertThat(fixture.setCommander(commander)).isSameAs(fixture);
        assertThat(playerData.getCommanderId()).isEqualTo(commanderId);
        verify(gameState).invalidateCache();
    }

    @Test
    void getCommander_WhenTheCommanderIdIsNull_ThenEmpty() {
        playerData.setCommanderId(null);
        when(gameState.getRuleset()).thenReturn(ruleset);
        assertThat(fixture.getCommander()).isEmpty();
    }

    @Test
    void getTeam() {
        assertThat(fixture.getTeam()).isEqualTo(playerData.getTeam());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void canSee(boolean canSee) {
        when(gameState.evaluate(new VisionCheckQuery(fixture, unit0))).thenReturn(canSee);
        assertThat(fixture.canSee(unit0)).isEqualTo(canSee);
    }

    @Test
    void getSpellSlots() {
        when(gameState.getRuleset()).thenReturn(ruleset);
        when(spellSlotHelper.getSpellSlots(ruleset, playerData)).thenReturn(List.of(spellSlot));
        assertThat(fixture.getSpellSlots()).containsExactly(spellSlot);
    }

    @Test
    void getChosenSpells() {
        when(gameState.getRuleset()).thenReturn(ruleset);
        when(spellSlotHelper.getChosenSpells(ruleset, playerData)).thenReturn(List.of(spell));
        assertThat(fixture.getChosenSpells()).containsExactly(spell);
    }

    @Test
    void setChosenSpells(SeededRng random) {
        var spellId = random.<SpellId>get();
        when(spell.getId()).thenReturn(spellId);
        assertThat(fixture.setChosenSpells(List.of(spell))).isSameAs(fixture);
        assertThat(playerData.getChosenSpells().getSpellIds()).containsExactly(spellId);
        verify(gameState).invalidateCache();
    }

    @Test
    void getSupplies() {
        assertThat(fixture.getSupplies()).isEqualTo(playerData.getSupplies());
    }

    @Test
    void setSupplies(SeededRng random) {
        var value = random.nextInt();
        assertThat(fixture.setSupplies(value)).isEqualTo(fixture);
        assertThat(fixture.getSupplies()).isEqualTo(value);
        assertThat(playerData.getSupplies()).isEqualTo(value);
        verify(gameState).invalidateCache();
    }

    @Test
    void getAether() {
        assertThat(fixture.getAether()).isEqualTo(playerData.getAether());
    }

    @Test
    void setAether(SeededRng random) {
        var value = random.nextInt();
        assertThat(fixture.setAether(value)).isEqualTo(fixture);
        assertThat(fixture.getAether()).isEqualTo(value);
        assertThat(playerData.getAether()).isEqualTo(value);
        verify(gameState).invalidateCache();
    }

    @Test
    void getFocus() {
        assertThat(fixture.getFocus()).isEqualTo(playerData.getFocus());
    }

    @Test
    void setFocus(SeededRng random) {
        var value = random.nextInt();
        assertThat(fixture.setFocus(value)).isEqualTo(fixture);
        assertThat(fixture.getFocus()).isEqualTo(value);
        assertThat(playerData.getFocus()).isEqualTo(value);
        verify(gameState).invalidateCache();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isDefeated(boolean defeated) {
        playerData.setDefeated(defeated);
        assertThat(fixture.isDefeated()).isEqualTo(defeated);
    }

    @Test
    void defeat() {
        playerData.setDefeated(false);
        assertThat(fixture.defeat()).isSameAs(fixture);
        assertThat(fixture.isDefeated()).isTrue();
        assertThat(playerData.isDefeated()).isTrue();
        verify(gameState).invalidateCache();
    }

    @Test
    void getStructures(SeededRng random) {
        when(gameState.getStructures())
                .thenReturn(new TreeMap<>(Map.of(random.get(), structure0, random.get(), structure1)));
        when(structure0.getOwner()).thenReturn(Optional.ofNullable(fixture));
        when(structure1.getOwner()).thenReturn(Optional.of(player));
        assertThat(fixture.getStructures()).containsOnly(structure0);
    }

    @Test
    void getUnits(SeededRng random) {
        when(gameState.getUnits()).thenReturn(new TreeMap<>(Map.of(random.get(), unit0, random.get(), unit1)));
        when(unit0.getOwner()).thenReturn(Optional.ofNullable(fixture));
        when(unit1.getOwner()).thenReturn(Optional.of(player));
        assertThat(fixture.getUnits()).containsOnly(unit0);
    }

    @Test
    void getOwner() {
        assertThat(fixture.getOwner()).contains(fixture);
    }

    @Test
    void getActions() {
        when(gameState.getRuleset()).thenReturn(ruleset);
        when(ruleset.getPassAction()).thenReturn(pass);
        when(ruleset.getYieldAction()).thenReturn(yield);
        when(spellSlotHelper.getSpellSlots(ruleset, playerData)).thenReturn(List.of(spellSlot));
        when(spellSlot.getSpell()).thenReturn(Optional.of(spell));
        assertThat(fixture.getActions()).containsExactly(spell, pass, yield);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getRules(boolean spellIsActive, SeededRng random) {
        var commanderId = random.<CommanderId>get();
        playerData.setCommanderId(commanderId);
        when(gameState.getRuleset()).thenReturn(ruleset);
        when(ruleset.getCommander(commanderId)).thenReturn(commander);
        when(spellSlotHelper.getSpellSlots(ruleset, playerData)).thenReturn(List.of(spellSlot));
        when(spellSlot.getSpell()).thenReturn(Optional.of(spell));
        when(spellSlot.isActive()).thenReturn(spellIsActive);
        if (spellIsActive) {
            assertThat(fixture.getRules()).containsExactly(commander, spell);
        } else {
            assertThat(fixture.getRules()).containsExactly(commander);
        }
    }

}
