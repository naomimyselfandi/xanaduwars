package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// An action an actor can take.
public interface Action {

    /// This action's name. Action names are not necessarily unique; in
    /// particular, multiple unit types can have abilities with the same name.
    Name name();

    /// A description of this action's targets.
    @Unmodifiable List<TargetSpec> targets();

    /// A script describing this action's preconditions. This script is
    /// evaluated before targets are chosen, so it cannot refer to them.
    Script precondition();

    /// A script used to validate this action. This script has access to both
    /// the actor and the target(s), if any.
    Script validation();

    /// A script describing this action's effect.
    Script effect();

    /// A script that calculates this action's supply cost.
    Script supplyCost();

    /// A script that calculates this action's aether cost.
    Script aetherCost();

}
