package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.targets;

import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Asset;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Structure;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Unit;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Weapon;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;

record TargetValidatorForWeapon() implements TargetValidator<Unit, Weapon, Asset> {

    @Override
    public boolean test(Unit actor, Weapon weapon, Asset target, TargetSpec spec) {
        return switch (target) {
            case Structure structure -> structure.getTags().stream().anyMatch(weapon.getDamage()::containsKey);
            case Unit unit -> weapon.getDamage().containsKey(unit.getType());
        };
    }

    @Override
    public Result.Fail fail() {
        return Result.fail("Cannot attack that with this weapon.");
    }

}
