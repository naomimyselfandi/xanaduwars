package io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.Version;

/// A factory that creates low-level game data.
public interface GameDataFactory {

    /// Copy low-level game state data. The copy is created in-memory, but may
    /// be subsequently persisted if desired.
    GameData create(GameData source);

    /// Create low-level game state data from a map. The data is created
    /// in-memory, but may be subsequently persisted if desired.
    GameData create(MapData map, Version version);

}
