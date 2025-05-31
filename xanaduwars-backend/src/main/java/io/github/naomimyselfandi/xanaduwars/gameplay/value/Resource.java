package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;

/// A resource that a player can spend.
@ExcludeFromCoverageReport
public enum Resource {

    /// The main resource used to deploy units and build structures.
    SUPPLIES,

    /// A secondary resource used to deploy advanced units and build structures.
    AETHER,

    /// The main resource used to cast spells.
    FOCUS,

}
