package io.github.naomimyselfandi.xanaduwars.gameplay.common;

import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;

/// Whether an action was successfully executed.
@ExcludeFromCoverageReport
public enum Execution {

    /// The action completed successfully.
    SUCCESSFUL,

    /// The action was interrupted by a previously unseen obstacle.
    INTERRUPTED,

}
