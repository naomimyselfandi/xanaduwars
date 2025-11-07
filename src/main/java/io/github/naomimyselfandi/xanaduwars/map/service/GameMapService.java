package io.github.naomimyselfandi.xanaduwars.map.service;

import io.github.naomimyselfandi.xanaduwars.map.dto.GameMapDto;
import io.github.naomimyselfandi.xanaduwars.map.dto.GameMapUpdateDto;
import io.github.naomimyselfandi.xanaduwars.map.entity.GameMap;
import io.github.naomimyselfandi.xanaduwars.util.Id;

/// A service for working with game maps.
public interface GameMapService {

    /// Load a game map by ID.
    GameMapDto get(Id<GameMap> id);

    /// Create a new game map.
    GameMapDto create(GameMapUpdateDto request);

    /// Update an existing game map.
    GameMapDto update(Id<GameMap> id, GameMapUpdateDto update);

    /// Update a game map's status.
    GameMapDto updateStatus(Id<GameMap> id, GameMap.Status status);

}
