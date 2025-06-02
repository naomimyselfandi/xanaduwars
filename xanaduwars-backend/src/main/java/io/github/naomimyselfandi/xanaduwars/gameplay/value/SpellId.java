package io.github.naomimyselfandi.xanaduwars.gameplay.value;

/// The ID of a spell.
public record SpellId(PlayerId ownerId, int index) implements ElementId {

    @Override
    public String toString() {
        return "Spell(%d, %d)".formatted(ownerId.intValue(), index);
    }

}
