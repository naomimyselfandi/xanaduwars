package io.github.naomimyselfandi.xanaduwars.core.ruleset;

import io.github.naomimyselfandi.xanaduwars.core.common.Name;
import io.github.naomimyselfandi.xanaduwars.core.common.TargetSpec;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// An action an actor can take.
public sealed interface Action permits AssetType, NormalAction, Spell, Weapon {

    /// This action's name. Weapon actions may share names with other actions;
    /// action names are otherwise unique.
    Name getName();

    /// A description of this action's targets.
    @Unmodifiable List<TargetSpec> getTargets();

    /// A script describing this action's preconditions. This script is
    /// evaluated before targets are chosen, so it cannot refer to them.
    Script getPrecondition();

    /// A script used to validate this action. This script has access to both
    /// the actor and the target(s), if any.
    Script getValidation();

    /// A script describing this action's effect.
    Script getEffect();

    /// A script that calculates this action's supply cost.
    Script getSupplyCost();

    /// A script that calculates this action's aether cost.
    Script getAetherCost();

    /// A script that calculates this action's focus cost.
    Script getFocusCost();

}
