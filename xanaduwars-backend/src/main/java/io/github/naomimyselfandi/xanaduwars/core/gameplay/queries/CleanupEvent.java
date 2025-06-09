package io.github.naomimyselfandi.xanaduwars.core.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.GameState;

/// An event indicating that cleanup tasks should be performed. This event is
/// evaluated at the end of action processing if at least one asset was
/// destroyed; it may be posted multiple times if handling it causes additional
/// assets to be destroyed.
public record CleanupEvent(GameState subject) implements Event {}
