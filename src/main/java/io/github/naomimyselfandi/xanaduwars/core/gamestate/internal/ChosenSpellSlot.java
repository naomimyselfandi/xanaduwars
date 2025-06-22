package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.SpellSlot;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.Bitset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Spell;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

record ChosenSpellSlot(@Getter @Nullable Spell spell, Bitset activation, Bitset revelation, int index)
        implements SpellSlot {

    @Override
    public boolean isRevealed() {
        return revelation.test(index);
    }

    @Override
    public SpellSlot reveal() {
        revelation.set(index, true);
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
