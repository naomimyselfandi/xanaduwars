package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TileDestroyedEvent;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// A rule that defeats a player when a tile is destroyed.
public record KeystoneRule(
        @Override @NotNull @Valid Filter<Tile> subjectFilter
) implements SubjectRule<TileDestroyedEvent, None, Tile> {

    @Override
    public None handle(TileDestroyedEvent query, None value) {
        query.subject().owner().ifPresent(Player::defeat);
        return None.NONE;
    }

}
