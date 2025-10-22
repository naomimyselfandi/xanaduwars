package io.github.naomimyselfandi.xanaduwars.core.model;

import io.github.naomimyselfandi.xanaduwars.core.message.Rule;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.stream.Stream;

/// A version of the game.
public interface Version extends ScriptRuntime {

    /// Get this version's version number.
    VersionNumber getVersionNumber();

    /// Get all the global versions declared by this version.
    @Unmodifiable List<Rule> getGlobalRules();

    /// Get all the declarations declared by this version of the game.
    Stream<Object> getDeclarations();

    /// Get the ability used for unit movement. This ability is automatically
    /// available to all units with positive speed.
    Ability getMoveAbility();

    /// Get the ability used to initiate combat. This ability is automatically
    /// available to all units with weapons.
    Ability getFireAbility();

    /// Get the ability used to drop off a unit. This ability is automatically
    /// available to all units carrying other units.
    Ability getDropAbility();

}
