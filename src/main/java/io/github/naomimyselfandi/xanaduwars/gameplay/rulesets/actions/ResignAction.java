package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Name;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// An action that causes a player to resign.
public record ResignAction(@Override @NotNull @Valid Name name) implements ActionWithoutTarget<Player> {

    @Override
    public Execution execute(Player user, None target) {
        user.defeat();
        return Execution.SUCCESSFUL;
    }

}
