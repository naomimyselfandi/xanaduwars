package io.github.naomimyselfandi.xanaduwars.core;

import io.github.naomimyselfandi.xanaduwars.ext.ConvenienceMixin;

/// A query representing a unanimous vote. Validation queries short circuit as
/// soon as `false` is encountered.
@ConvenienceMixin
public interface Validation extends DefaultQuery<Boolean> {

    @Override
    default Boolean defaultValue() {
        return true;
    }

    @Override
    default boolean shouldShortCircuit(Boolean value) {
        return !value;
    }

}
