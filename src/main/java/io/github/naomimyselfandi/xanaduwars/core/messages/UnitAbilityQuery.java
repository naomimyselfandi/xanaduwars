package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.model.Ability;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;

import java.util.ArrayList;
import java.util.List;

/// A query that determines the abilities a unit can use.
public record UnitAbilityQuery(Unit unit) implements SimpleQuery<List<Ability>> {

    @Override
    public List<Ability> defaultValue(ScriptRuntime runtime) {
        var base = unit.getType().getAbilities();
        var version = unit.getGameState().getVersion();
        var result = new ArrayList<Ability>(base.size() + 3);
        if (unit.getSpeed() > 0) {
            result.add(version.getMoveAbility());
        }
        if (!unit.getWeapons().isEmpty()) {
            result.add(version.getFireAbility());
        }
        result.addAll(base);
        if (unit.getUnit() != null) {
            result.add(version.getDropAbility());
        }
        return result;
    }

}
