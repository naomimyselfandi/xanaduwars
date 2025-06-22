package io.github.naomimyselfandi.xanaduwars.core.scripting;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public record BazEvent(@Nullable Object subject, Script before, Script after) implements SimpleEvent {

    @Override
    public List<Script> prologue() {
        return List.of(before);
    }

    @Override
    public List<Script> epilogue() {
        return List.of(after);
    }

}
