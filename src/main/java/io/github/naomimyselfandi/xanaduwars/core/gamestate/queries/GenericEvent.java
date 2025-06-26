package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.scripting.SimpleEvent;
import org.jetbrains.annotations.Nullable;

/// An event indicating that the game state changed in an unspecified way.
public record GenericEvent(@Nullable Object subject) implements SimpleEvent {}
