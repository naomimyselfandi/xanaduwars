package io.github.naomimyselfandi.xanaduwars.core.script;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

record LibraryImpl(@Unmodifiable Map<String, Object> members) implements Library {

    LibraryImpl(Map<String, Object> members) {
        this.members = Map.copyOf(members);
    }

    @Override
    public @Nullable Object lookup(String name) {
        return members.get(name);
    }

}
