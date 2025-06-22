package io.github.naomimyselfandi.xanaduwars.core.gamestate.service;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.GameStateData;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.Version;
import io.github.naomimyselfandi.xanaduwars.map.dto.MapDto;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NoSuchEntityException;

/// A service that manages low-level game data.
public interface GameStateDataService {

    /// Find low-level game data by ID.
    GameStateData get(Id<GameStateData> id) throws NoSuchEntityException;

    /// Create low-level game data using the given map and version.
    GameStateData create(MapDto map, Version version);

}
