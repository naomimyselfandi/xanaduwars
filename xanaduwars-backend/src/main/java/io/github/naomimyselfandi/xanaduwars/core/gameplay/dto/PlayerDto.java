package io.github.naomimyselfandi.xanaduwars.core.gameplay.dto;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Team;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.CommanderId;
import lombok.Data;

import java.util.List;

/// A DTO used to represent a player.
@Data
public class PlayerDto {
    private PlayerId id;
    private Team team;
    private CommanderId commander;
    private List<SpellSlotDto> spellSlots = List.of();
    private int supplies, aether, focus;
    private boolean defeated;
}
