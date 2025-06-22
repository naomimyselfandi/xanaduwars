package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.github.naomimyselfandi.xanaduwars.core.common.Name;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/// A single target in a command DTO.
public record CommandItemDto(@NotNull @Valid Name name, @NotNull List<@NotNull @Valid TargetRefDto> targets) {

    /// A single target in a command DTO.
    /// @implSpec This constructor takes an immutable copy of its list argument.
    @JsonCreator
    public CommandItemDto(Name name, List<TargetRefDto> targets) {
        this.name = name;
        this.targets = List.copyOf(targets);
    }

}
