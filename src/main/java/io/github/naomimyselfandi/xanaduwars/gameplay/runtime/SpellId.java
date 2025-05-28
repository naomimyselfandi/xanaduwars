package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

/// The ID of a spell.
public record SpellId(int ownerId, int index) {

    @Override
    public String toString() {
        return "Spell(%d, %d)".formatted(ownerId, index);
    }

}
