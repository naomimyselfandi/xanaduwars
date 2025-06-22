package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import io.github.naomimyselfandi.xanaduwars.core.common.CommanderId;
import io.github.naomimyselfandi.xanaduwars.core.common.SpellId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// A DTO representing a player's pregame choices.
@Data
public class ChoiceDto {
    private @Nullable @Valid CommanderId commander;
    private @NotNull List<@NotNull @Valid SpellId> spells;
}
