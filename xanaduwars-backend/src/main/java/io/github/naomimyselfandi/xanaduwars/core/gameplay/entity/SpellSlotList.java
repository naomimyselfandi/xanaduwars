package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/// A low-level description of a player's spell slot.
public record SpellSlotList(@Unmodifiable List<@NotNull @Valid SpellSlotData> slots) {

    static final SpellSlotList NONE = new SpellSlotList(List.of());

    /// A low-level description of a player's spell slot.
    /// @implSpec This constructor takes an immutable copy of its argument.
    public SpellSlotList(List<SpellSlotData> slots) {
        this.slots = List.copyOf(slots);
    }

}
