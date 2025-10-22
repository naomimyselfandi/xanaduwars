package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.util.NotCovered;
import org.jetbrains.annotations.Nullable;

/// An event indicating an object's state changed in some unspecified way.
/// Generic events are used to notify event listeners of changes when the
/// specifics don't matter, and game rules shouldn't handle them.
@NotCovered // Trivial
public record GenericEvent(@Nullable Object object) implements SimpleEvent {}
