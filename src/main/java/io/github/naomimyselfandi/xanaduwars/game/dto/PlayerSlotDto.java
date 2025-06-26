package io.github.naomimyselfandi.xanaduwars.game.dto;

import io.github.naomimyselfandi.xanaduwars.account.dto.BaseAccountDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/// A DTO representing a player slot.
@Data
public class PlayerSlotDto {
    private PlayerId id;
    private @Nullable BaseAccountDto account;
}
