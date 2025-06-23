package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Asset;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Structure;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Weapon;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;

import java.util.Comparator;
import java.util.Objects;

/// A query that calculates the damage a unit inflicts to a target.
public record AttackDamageQuery(Unit subject, Weapon weapon, Asset target, boolean counter) implements Query<Integer> {

    @Override
    public Integer defaultValue() {
        return switch (target) {
            case Unit unit -> weapon.getDamage().getOrDefault(unit.getType(), 0);
            case Structure structure -> structure
                    .getTags()
                    .stream()
                    .map(weapon.getDamage()::get)
                    .filter(Objects::nonNull)
                    .max(Comparator.naturalOrder())
                    .orElse(0);
        };
    }

}
