package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.SpellSlotData;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Spell;

record SpellSlotImpl(SpellSlotData spellSlotData, Ruleset ruleset) implements SpellSlot {

    @Override
    public Spell spell() {
        return ruleset.spells().get(spellSlotData.spellId().index());
    }

    @Override
    public boolean revealed() {
        return spellSlotData.revealed();
    }

    @Override
    public int timesCastThisTurn() {
        return spellSlotData.casts();
    }

    @Override
    public String toString() {
        return "SpellSlot[spell=%s]".formatted(spell());
    }

}
