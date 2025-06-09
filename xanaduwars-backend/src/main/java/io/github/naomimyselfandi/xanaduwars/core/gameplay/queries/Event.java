package io.github.naomimyselfandi.xanaduwars.core.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.core.scripting.Query;
import io.github.naomimyselfandi.xanaduwars.ext.None;

/// A query that doesn't produce a value.
public interface Event extends Query<None> {

    @Override
    default None defaultValue() {
        return None.NONE;
    }

}
