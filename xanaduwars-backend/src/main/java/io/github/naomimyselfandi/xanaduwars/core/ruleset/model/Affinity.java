package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;

/// An affinity a commander can have for a spell tag. This influences which
/// spells a player playing as the commander can choose.
@ExcludeFromCoverageReport
public enum Affinity {

    /// A positive affinity.
    POSITIVE,

    /// A negative affinity.
    NEGATIVE,

}
