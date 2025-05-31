package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.Player;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Name;
import io.github.naomimyselfandi.xanaduwars.ext.None;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// An action that ends a player's turn.
public record PassAction(@Override @NotNull @Valid Name name) implements ActionWithoutTarget<Player> {

    @Override
    public Execution execute(Player user, None target) {
        user.gameState().pass();
        return Execution.SUCCESSFUL;
    }

}
