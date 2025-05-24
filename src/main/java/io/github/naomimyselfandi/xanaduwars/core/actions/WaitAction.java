package io.github.naomimyselfandi.xanaduwars.core.actions;

import io.github.naomimyselfandi.xanaduwars.core.Execution;
import io.github.naomimyselfandi.xanaduwars.core.Unit;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// An action that causes a unit to do nothing.
public record WaitAction(
        @Override @NotNull @Valid Name name,
        @Override @NotNull TagSet tags
) implements ActionWithoutTarget<Unit> {

    @Override
    public Execution execute(Unit user, None target) {
        return Execution.SUCCESSFUL;
    }

}
