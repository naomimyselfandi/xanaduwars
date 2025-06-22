package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/// A DTO representing a command issued by a player.
public record CommandDto(@NotNull @Valid ActorRefDto actor, @NotNull List<@NotNull @Valid CommandItemDto> items) {

    /// A DTO representing a command issued by a player.
    /// @implSpec This constructor takes an immutable copy of its list argument.
    @JsonCreator
    public CommandDto(ActorRefDto actor, List<CommandItemDto> items) {
        this.actor = actor;
        this.items = List.copyOf(items);
    }

}
