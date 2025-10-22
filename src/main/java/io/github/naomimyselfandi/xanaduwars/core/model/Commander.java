package io.github.naomimyselfandi.xanaduwars.core.model;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A playable commander.
public interface Commander extends Specification {

    /// Get this commander's unique name.
    @Override
    String getName();

    /// Get this commander's signature spell or spells.
    @Unmodifiable List<Ability> getSignatureSpells();

    /// Get the ability tags this commander has a positive affinity for.
    /// Affinities govern spell selection as defined by game rules.
    @Unmodifiable List<AbilityTag> getPositiveAffinities();

    /// Get the ability tags this commander has a negative affinity for.
    /// Affinities govern spell selection as defined by game rules.
    @Unmodifiable List<AbilityTag> getNegativeAffinities();

    /// Get the number of spells a player playing as this commander can choose.
    int getChosenSpells();

}
