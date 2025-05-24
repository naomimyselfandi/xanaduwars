package io.github.naomimyselfandi.xanaduwars.core.runtime;

record SpellId(int ownerId, int index) {

    @Override
    public String toString() {
        return "Spell(%d, %d)".formatted(ownerId, index);
    }

}
