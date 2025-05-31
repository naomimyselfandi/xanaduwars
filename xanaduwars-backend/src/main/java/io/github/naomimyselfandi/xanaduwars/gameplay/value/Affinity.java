package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;

/// A commander's affinity for a spell. Affinities govern which combinations of
/// spells a commander can have access to.
@ExcludeFromCoverageReport
public enum Affinity {

    /// A positive affinity. Positive affinities have no direct effect, but
    /// interact with [negative][#NEGATIVE] affinities.
    POSITIVE,

    /// A negative affinity. If a player has picked a spell for which their
    /// commander has a negative affinity, they cannot pick other spells for
    /// which their commander does not have a positive affinity.
    NEGATIVE,

}
