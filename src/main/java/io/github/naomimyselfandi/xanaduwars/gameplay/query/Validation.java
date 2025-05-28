package io.github.naomimyselfandi.xanaduwars.gameplay.query;

import io.github.naomimyselfandi.xanaduwars.ext.ConvenienceMixin;
import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;

/// A query representing a unanimous vote. Validation queries short circuit as
/// soon as `false` is encountered.
@ConvenienceMixin
@ExcludeFromCoverageReport
public interface Validation extends Query<Boolean> {

    @Override
    default Boolean defaultValue() {
        return true;
    }

    @Override
    default boolean shouldShortCircuit(Boolean value) {
        return !value;
    }

}
