package io.github.naomimyselfandi.xanaduwars.core;

import io.github.naomimyselfandi.xanaduwars.ext.ConvenienceMixin;
import io.github.naomimyselfandi.xanaduwars.ext.None;

/// A query that notifies game rules that something happened. Events have side
/// effects and should only be evaluated when the appropriate situation has
/// occurred.
@ConvenienceMixin
public interface Event extends DefaultQuery<None> {

    @Override
    default None defaultValue() {
        return None.NONE;
    }

}
