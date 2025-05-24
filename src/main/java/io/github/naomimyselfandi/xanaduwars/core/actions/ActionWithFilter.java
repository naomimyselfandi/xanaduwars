package io.github.naomimyselfandi.xanaduwars.core.actions;

import io.github.naomimyselfandi.xanaduwars.core.Action;
import io.github.naomimyselfandi.xanaduwars.core.Element;
import io.github.naomimyselfandi.xanaduwars.core.filter.BiFilter;

/// An action with a usage filter.
public interface ActionWithFilter<S extends Element, T> extends Action<S, T> {

    /// The filter that defines valid inputs to this action.
    BiFilter<S, T> filter();

}
