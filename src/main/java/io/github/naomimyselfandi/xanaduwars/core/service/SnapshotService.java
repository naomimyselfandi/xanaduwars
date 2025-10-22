package io.github.naomimyselfandi.xanaduwars.core.service;

import io.github.naomimyselfandi.xanaduwars.core.model.GameState;

/// A service that converts game states to and from snapshots.
public interface SnapshotService {

    /// Load a game state from a snapshot.
    GameState load(Snapshot snapshot);

    /// Save a snapshot of a game state.
    Snapshot save(GameState gameState);

}
