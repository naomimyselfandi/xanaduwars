package io.github.naomimyselfandi.xanaduwars.core.scripting;

import org.jetbrains.annotations.Nullable;

public record FooQuery(@Nullable Object subject, Integer defaultValue) implements Query<Integer> {}
