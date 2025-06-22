package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import io.github.naomimyselfandi.xanaduwars.core.common.CommanderId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.Team;
import lombok.Data;

import java.util.List;

/// A DTO representing a player.
@Data
public class PlayerDto {
    private PlayerId id;
    private CommanderId commander;
    private Team team;
    private List<SpellSlotDto> spellSlots;
    private int supplies, aether, focus;
    private boolean active, defeated;
}
