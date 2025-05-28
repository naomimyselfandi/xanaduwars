package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TurnStartEvent;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TurnStartEventForTile;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.TurnStartEventForUnit;
import io.github.naomimyselfandi.xanaduwars.ext.None;

/// A rule that handles the standard start-of-turn bookkeeping.
public record TurnStartRule() implements GameRule<TurnStartEvent, None> {

    @Override
    public boolean handles(TurnStartEvent query, None value) {
        return true;
    }

    @Override
    public None handle(TurnStartEvent query, None value) {
        var player = query.subject();
        var gameState = player.gameState();
        player.clearActiveSpells();
        player.tiles().map(TurnStartEventForTile::new).forEach(gameState::evaluate);
        player.units().map(TurnStartEventForUnit::new).forEach(gameState::evaluate);
        return None.NONE;
    }

}
