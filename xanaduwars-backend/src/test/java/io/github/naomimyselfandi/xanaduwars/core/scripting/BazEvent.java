package io.github.naomimyselfandi.xanaduwars.core.scripting;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.Event;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public record BazEvent(@Nullable Object subject, Script before, Script after) implements Event {

    @Override
    public Stream<Script> prologue() {
        return Stream.of(before);
    }

    @Override
    public Stream<Script> epilogue() {
        return Stream.of(after);
    }

}
