package io.github.naomimyselfandi.xanaduwars.core.gameplay.state;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.SpellSlotData;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Spell;

record SpellSlotImpl(SpellSlotData spellSlotData, Ruleset ruleset) implements SpellSlot {

    @Override
    public Spell getSpell() {
        return ruleset.getSpells().get(spellSlotData.spellId().index());
    }

    @Override
    public boolean isRevealed() {
        return spellSlotData.revealed();
    }

    @Override
    public int getCastsThisTurn() {
        return spellSlotData.casts();
    }

    @Override
    public String toString() {
        return "SpellSlot[spell=%s]".formatted(getSpell());
    }

}
