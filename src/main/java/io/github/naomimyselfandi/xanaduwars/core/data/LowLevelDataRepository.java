package io.github.naomimyselfandi.xanaduwars.core.data;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/// A repository for managing low-level map and game state data.
public interface LowLevelDataRepository extends CrudRepository<LowLevelData, UUID> {}
