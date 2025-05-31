package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filter;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TurnStartEventForTile;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import jakarta.validation.constraints.NotNull;

/// A rule that handles start-of-turn income.
public record TileIncomeRule(
        @Override @NotNull Filter<Tile> subjectFilter
) implements SubjectRule<TurnStartEventForTile, None, Tile> {

    @Override
    public None handle(TurnStartEventForTile query, None value) {
        var tile = query.subject();
        var income = tile.income();
        var player = tile.owner().orElseThrow();
        var resources = player.resources();
        for (var resource : Resource.values()) {
            var total = resources.getOrDefault(resource, 0) + income.getOrDefault(resource, 0);
            player.resource(resource, total);
        }
        return None.NONE;
    }

}
