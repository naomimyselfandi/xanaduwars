package io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/// A repository for managing low-level game state data.
public interface GameDataRepository extends CrudRepository<GameData, UUID> {}
