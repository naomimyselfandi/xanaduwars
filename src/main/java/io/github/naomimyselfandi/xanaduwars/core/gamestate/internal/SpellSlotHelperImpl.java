package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.SpellSlot;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.PlayerData;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Commander;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Spell;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
class SpellSlotHelperImpl implements SpellSlotHelper {

    @Override
    public List<SpellSlot> getSpellSlots(Ruleset ruleset, PlayerData playerData) {
        var signatureSpells = Stream
                .ofNullable(playerData.getCommanderId())
                .map(ruleset::getCommander)
                .map(Commander::getSignatureSpells)
                .flatMap(List::stream)
                .toList();
        var chosenSpells = getChosenSpells(ruleset, playerData);
        return Stream.concat(
                IntStream.range(0, signatureSpells.size()).mapToObj(signatureSpellFactory(signatureSpells, playerData)),
                IntStream.range(0, chosenSpells.size()).mapToObj(chosenSpellFactory(chosenSpells, playerData))
        ).toList();
    }

    @Override
    public List<Spell> getChosenSpells(Ruleset ruleset, PlayerData playerData) {
        return playerData
                .getChosenSpells()
                .getSpellIds()
                .stream()
                .map(id -> id == null ? null : ruleset.getSpell(id))
                .toList();
    }

    private IntFunction<SpellSlot> signatureSpellFactory(List<Spell> spells, PlayerData playerData) {
        var activation = playerData.getSignatureSpellActivation();
        return i -> new SignatureSpellSlot(spells.get(i), activation, i);
    }

    private IntFunction<SpellSlot> chosenSpellFactory(List<Spell> spells, PlayerData playerData) {
        var activation = playerData.getChosenSpellActivation();
        var revelation = playerData.getChosenSpellRevelation();
        return i -> new ChosenSpellSlot(spells.get(i), activation, revelation, i);
    }

}
