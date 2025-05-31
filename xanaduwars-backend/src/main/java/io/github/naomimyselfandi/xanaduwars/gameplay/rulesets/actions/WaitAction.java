package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Name;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// An action that causes a unit to do nothing.
public record WaitAction(@Override @NotNull @Valid Name name) implements ActionWithoutTarget<Unit> {

    @Override
    public Execution execute(Unit user, None target) {
        return Execution.SUCCESSFUL;
    }

}
