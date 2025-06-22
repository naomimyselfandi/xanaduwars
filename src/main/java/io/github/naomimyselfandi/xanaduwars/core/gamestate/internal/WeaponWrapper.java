package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.MaxRangeQuery;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.MinRangeQuery;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Weapon;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

record WeaponWrapper(Unit unit, @Delegate(excludes = Excludes.class) Weapon weapon) implements Weapon {

    @Override
    public @Unmodifiable List<TargetSpec> getTargets() {
        var game = unit.getGameState();
        return weapon
                .getTargets()
                .stream()
                .map(targetSpec -> {
                    var minRange = game.evaluate(new MinRangeQuery(unit, weapon, targetSpec));
                    var maxRange = game.evaluate(new MaxRangeQuery(unit, weapon, targetSpec));
                    return targetSpec.withMinRange(minRange).withMaxRange(maxRange);
                })
                .toList();
    }

    private interface Excludes {
        @SuppressWarnings("unused") List<TargetSpec> getTargets();
    }

}
