package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.StructureTypeId;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.Serializable;
import java.util.Map;

/// The memory various players have of a tile. This is used to retain
/// information about previously seen structures.
public record Memory(@Unmodifiable Map<@NotNull @Valid PlayerId, @NotNull @Valid StructureTypeId> memory)
        implements Serializable {

    static final Memory NONE = new Memory(Map.of());

    /// The memory various players have of a tile. This is used to retain
    /// information about previously seen structures.
    /// @implSpec This constructor takes an immutable copy of its argument.
    public Memory(Map<PlayerId, StructureTypeId> memory) {
        this.memory = Map.copyOf(memory);
    }

}
