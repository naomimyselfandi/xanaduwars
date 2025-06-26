package io.github.naomimyselfandi.xanaduwars.game.entity;

import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/// A repository that holds games.
public interface GameRepository extends JpaRepository<Game, Id<Game>>,JpaSpecificationExecutor<Game> {}
