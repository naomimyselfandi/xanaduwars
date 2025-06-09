package io.github.naomimyselfandi.xanaduwars.core.gameplay.dto;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.SpellId;
import lombok.Data;

/// A DTO used to represent a spell slot.
@Data
public class SpellSlotDto {
    private SpellId spell;
    private boolean revealed;
    private int casts;
}
