package io.github.naomimyselfandi.xanaduwars.core.actions;

import io.github.naomimyselfandi.xanaduwars.core.Execution;
import io.github.naomimyselfandi.xanaduwars.core.Player;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// An action that causes a player to resign.
public record ResignAction(
        @Override @NotNull @Valid Name name,
        @Override @NotNull TagSet tags
) implements ActionWithoutTarget<Player> {

    @Override
    public Execution execute(Player user, None target) {
        user.defeat();
        return Execution.SUCCESSFUL;
    }

}
