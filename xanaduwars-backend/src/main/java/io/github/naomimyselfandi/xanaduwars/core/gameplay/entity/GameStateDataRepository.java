package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import org.springframework.data.repository.CrudRepository;

/// A repository that stores game state data.
public interface GameStateDataRepository extends CrudRepository<GameStateData, GameStateId> {}
