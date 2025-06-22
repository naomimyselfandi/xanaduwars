package io.github.naomimyselfandi.xanaduwars.core.gamestate.entity;

import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/// A repository that stores game state data.
public interface GameStateDataRepository extends JpaRepository<GameStateData, Id<GameStateData>>,
        JpaSpecificationExecutor<GameStateData> {}
