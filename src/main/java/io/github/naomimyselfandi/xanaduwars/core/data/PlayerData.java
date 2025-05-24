package io.github.naomimyselfandi.xanaduwars.core.data;

import io.github.naomimyselfandi.xanaduwars.core.Resource;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.PlayerId;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/// Low-level data about the state of a tile.
@Data
@Embeddable
@FieldNameConstants(asEnum = true)
public class PlayerData {

    /// The player's ID.
    private @NotNull PlayerId playerId;

    /// The team the player belongs to.
    private @NotNull @PositiveOrZero Integer team;

    /// Whether the player has been defeated.
    private boolean defeated;

    /// The index of the player's type.
    private @NotNull @PositiveOrZero Integer playerType;

    @JdbcTypeCode(SqlTypes.JSON)
    private @Unmodifiable Map<@NotNull Resource, @NotNull @PositiveOrZero Integer> resources = Arrays
            .stream(Resource.values())
            .collect(Collectors.toUnmodifiableMap(Function.identity(), _ -> 0));

    /// The player's current resource levels.
    public @Unmodifiable Map<@NotNull Resource, @NotNull @PositiveOrZero Integer> resources() {
        return Map.copyOf(resources);
    }

    /// The player's current resource levels.
    public PlayerData resources(Map<Resource, Integer> resources) {
        resources = new EnumMap<>(resources);
        for (var resource : Resource.values()) {
            resources.putIfAbsent(resource, 0);
        }
        this.resources = Map.copyOf(resources);
        return this;
    }

    @JdbcTypeCode(SqlTypes.JSON)
    private @Unmodifiable List<@NotNull @PositiveOrZero Integer> knownSpells = List.of();

    /// The spells the player knows.
    public @Unmodifiable List<@NotNull @PositiveOrZero Integer> knownSpells() {
        return List.copyOf(knownSpells);
    }

    /// The spells the player knows.
    public PlayerData knownSpells(List<Integer> knownSpells) {
        this.knownSpells = List.copyOf(knownSpells);
        return this;
    }

    @JdbcTypeCode(SqlTypes.JSON)
    private @Unmodifiable List<@NotNull @PositiveOrZero Integer> activeSpells = List.of();

    /// The spells the player currently has active.
    public @Unmodifiable List<@NotNull @PositiveOrZero Integer> activeSpells() {
        return List.copyOf(activeSpells);
    }

    /// The spells the player currently has active.
    public PlayerData activeSpells(List<Integer> activeSpells) {
        this.activeSpells = List.copyOf(activeSpells);
        return this;
    }

}
