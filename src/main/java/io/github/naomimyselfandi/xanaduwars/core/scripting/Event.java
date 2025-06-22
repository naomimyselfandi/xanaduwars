package io.github.naomimyselfandi.xanaduwars.core.scripting;

/// A query representing a game event.
///
/// @param <T> The type of value produced when this query is evaluated.
/// @see SimpleEvent A convenience mixin is availabe for events that don't
/// return anything.
public interface Event<T> extends Query<T> {}
