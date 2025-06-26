package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.auth.Bypassable;
import io.github.naomimyselfandi.xanaduwars.auth.service.AuthService;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.game.entity.PlayerSlot;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class GameGuard {

    private final AuthService authService;

    public boolean isPlayer(Game game) {
        return (authService.getId().equals(game.getHost().getId()))
                || game.getPlayerSlots().keySet().stream().anyMatch(playerId -> isPlayer(game, playerId));
    }

    public boolean isPlayer(Game game, PlayerId playerId) {
        return Optional
                .ofNullable(game.getPlayerSlots().get(playerId))
                .map(PlayerSlot::getAccount)
                .map(Account::getId)
                .filter(authService.getId()::equals)
                .isPresent();
    }

    @Bypassable(Role.JUDGE)
    public boolean canViewMetadata(Game game) {
        return isPlayer(game);
    }

    @Bypassable(Role.JUDGE)
    public boolean canView(Game game, @Nullable PlayerId playerId) {
        return playerId == null ? game.getStatus() == Game.Status.FINISHED : isPlayer(game, playerId);
    }

    public boolean isActivePlayer(Game game) {
        return switch (game.getStatus()) {
            case ONGOING -> {
                var activePlayerIndex = game.getGameStateData().getTurn().ordinal() % game.getPlayerSlots().size();
                yield isPlayer(game, new PlayerId(activePlayerIndex));
            }
            case PENDING, FINISHED, CANCELED -> false;
        };
    }

}
