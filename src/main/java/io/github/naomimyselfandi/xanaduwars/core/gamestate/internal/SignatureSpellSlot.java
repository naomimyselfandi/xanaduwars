package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.SpellSlot;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.Bitset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Spell;
import lombok.Getter;

record SignatureSpellSlot(@Getter Spell spell, Bitset activation, int index) implements SpellSlot {

    @Override
    public boolean isRevealed() {
        return true;
    }

    @Override
    public SpellSlot reveal() {
        return this;
    }

    @Override
    public boolean isActive() {
        return activation.test(index);
    }

    @Override
    public SpellSlot setActive(boolean active) {
        activation.set(index, active);
        return this;
    }

}
