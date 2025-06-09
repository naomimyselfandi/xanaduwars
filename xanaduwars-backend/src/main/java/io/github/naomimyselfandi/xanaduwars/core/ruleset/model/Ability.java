package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

/// A custom action which can be used by a unit.
public interface Ability extends Action {

    /// This ability's minimum range. This only applies to the ability's first
    /// target; if the first target is not part of the map, this is ignored.
    int minimumRange();

    /// This ability's maximum range. This only applies to the ability's first
    /// target; if the first target is not part of the map, this is ignored.
    int maximumRange();

}
