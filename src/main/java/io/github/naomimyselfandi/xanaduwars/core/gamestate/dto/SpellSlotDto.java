package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import io.github.naomimyselfandi.xanaduwars.core.common.SpellId;
import lombok.Data;

/// A DTO representing a spell slot.
@Data
public class SpellSlotDto {
    private SpellId spell;
    private boolean active, revealed;
}
