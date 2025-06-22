package io.github.naomimyselfandi.xanaduwars.core.scripting;

import org.jetbrains.annotations.Nullable;

public record BarEvent(@Nullable Object subject) implements Event<None> {

    @Override
    public None defaultValue() {
        return None.NONE;
    }

}
