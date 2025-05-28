package io.github.naomimyselfandi.xanaduwars.gameplay.query;

import io.github.naomimyselfandi.xanaduwars.ext.ConvenienceMixin;
import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;
import io.github.naomimyselfandi.xanaduwars.ext.None;

/// A query that notifies game rules that something happened. Events have side
/// effects and should only be evaluated when the appropriate situation has
/// occurred.
@ConvenienceMixin
@ExcludeFromCoverageReport
public interface Event extends Query<None> {

    @Override
    default None defaultValue() {
        return None.NONE;
    }

}
