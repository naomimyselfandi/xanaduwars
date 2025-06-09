package io.github.naomimyselfandi.xanaduwars.core.scripting;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.queries.Event;
import org.jetbrains.annotations.Nullable;

public record BarEvent(@Nullable Object subject) implements Event {}
