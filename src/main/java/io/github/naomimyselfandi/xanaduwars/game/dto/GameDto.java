package io.github.naomimyselfandi.xanaduwars.game.dto;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import lombok.Data;

import java.util.List;

/// A DTO representing a game.
@Data
public class GameDto {
    private Id<Game> id;
    private int turnCount;
    private Game.Status status;
    private Id<Account> host;
    private List<PlayerSlotDto> playerSlots;
}
