package io.github.naomimyselfandi.xanaduwars.core.queries;

import io.github.naomimyselfandi.xanaduwars.core.Action;
import io.github.naomimyselfandi.xanaduwars.core.Validation;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A query that checks whether some actions may be used together.
public record ActionGroupValidation(@Unmodifiable List<? extends Action<?, ?>> actions) implements Validation {

    /// A query that checks whether some actions may be used together.
    /// @implSpec This constructor takes an immutable copy of its argument.
    public ActionGroupValidation(List<? extends Action<?, ?>> actions) {
        this.actions = List.copyOf(actions);
    }

}
