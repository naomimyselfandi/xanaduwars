package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.SpellSlot;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.PlayerData;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Spell;

import java.util.List;

interface SpellSlotHelper {

    List<SpellSlot> getSpellSlots(Ruleset ruleset, PlayerData playerData);

    List<Spell> getChosenSpells(Ruleset ruleset, PlayerData playerData);

}
