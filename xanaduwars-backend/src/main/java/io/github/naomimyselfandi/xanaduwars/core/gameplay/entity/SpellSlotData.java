package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.SpellId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.With;
import org.intellij.lang.annotations.RegExp;

import java.io.Serializable;

/// A low-level description of a spell slot.
public record SpellSlotData(@NotNull @Valid SpellId spellId, @With boolean revealed, @PositiveOrZero @With int casts)
        implements Serializable {

    private static final String REVEALED = "!";
    private static final String HIDDEN = "?";
    private static final @RegExp String SPLITTER = "[" + REVEALED + HIDDEN + "]";

    static SpellSlotData fromString(String string) {
        var parts = string.split(SPLITTER);
        var spellId = new SpellId(Integer.parseInt(parts[0]));
        var revealed = string.contains(REVEALED);
        var casts = Integer.parseInt(parts[1]);
        return new SpellSlotData(spellId, revealed, casts);
    }

    @Override
    public String toString() {
        return "%d%s%d".formatted(spellId.index(), revealed ? REVEALED : HIDDEN, casts);
    }

}
