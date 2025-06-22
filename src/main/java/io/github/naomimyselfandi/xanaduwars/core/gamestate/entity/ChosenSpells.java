package io.github.naomimyselfandi.xanaduwars.core.gamestate.entity;

import io.github.naomimyselfandi.xanaduwars.core.common.SpellId;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/// A list of a player's chosen spells.
@Data
@Embeddable
public class ChosenSpells implements Serializable {

    private static final String DELIMITER = ",";

    @Column(name = "chosen_spells")
    private String spellIds = "";

    /// Set the IDs of the player's chosen spells.
    public List<SpellId> getSpellIds() {
        return spellIds.isEmpty() ? List.of() : Arrays
                .stream(spellIds.split(DELIMITER))
                .map(string -> string.isEmpty() ? null : new SpellId(Integer.parseInt(string)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /// Set the IDs of the player's chosen spells.
    public ChosenSpells setSpellIds(List<@Nullable SpellId> spellIds) {
        this.spellIds = spellIds
                .stream()
                .map(spellId -> spellId == null ? "" : String.valueOf(spellId.index()))
                .collect(Collectors.joining(DELIMITER));
        return this;
    }

}
