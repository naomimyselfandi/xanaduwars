package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.scripting.SimpleEvent;

/// An event indicating the game should be cleaned up. A cleanup event is
/// evaluated at the start of the game and whenever a command is fully
/// processed.
public record CleanupEvent() implements SimpleEvent {}
