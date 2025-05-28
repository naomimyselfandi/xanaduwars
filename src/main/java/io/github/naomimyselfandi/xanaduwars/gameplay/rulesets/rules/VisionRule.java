package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.queries.VisionQuery;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.VisionQueryStage;

import java.util.stream.Stream;

/// A rule that handles the main vision logic.
public record VisionRule() implements GameRule.Validator<VisionQuery> {

    @Override
    public boolean isValid(VisionQuery query) {
        var player = query.subject();
        var target = query.target();
        return Stream
                .concat(player.tiles(), player.units())
                .map(subject -> new VisionQueryStage(subject, target))
                .anyMatch(player.gameState()::evaluate);
    }

}
