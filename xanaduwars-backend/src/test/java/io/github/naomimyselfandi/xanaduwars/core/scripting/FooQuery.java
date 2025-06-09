package io.github.naomimyselfandi.xanaduwars.core.scripting;

public record FooQuery(Object subject, Integer defaultValue) implements Query<Integer> {}
