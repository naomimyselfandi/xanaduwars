package io.github.naomimyselfandi.xanaduwars.core.scripting;

import org.jetbrains.annotations.Nullable;

public record BatQuery(@Nullable Object subject, Result defaultValue) implements Query<Result> {}
