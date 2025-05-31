package io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.CommanderId;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.PlayerId;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.SpellTypeId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
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
    @Embedded
    private @NotNull PlayerId playerId;

    /// The team the player belongs to.
    private @NotNull @PositiveOrZero Integer team;

    /// Whether the player has been defeated.
    private boolean defeated;

    /// The index of the player's type.
    private @NotNull CommanderId commander;

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
    public @Unmodifiable List<@NotNull SpellTypeId> knownSpells() {
        return knownSpells.stream().map(SpellTypeId::new).toList();
    }

    /// The spells the player knows.
    public PlayerData knownSpells(List<SpellTypeId> knownSpells) {
        this.knownSpells = knownSpells.stream().map(SpellTypeId::index).toList();
        return this;
    }

    @JdbcTypeCode(SqlTypes.JSON)
    private @Unmodifiable List<@NotNull @PositiveOrZero Integer> activeSpells = List.of();

    /// The spells the player currently has active.
    public @Unmodifiable List<@NotNull SpellTypeId> activeSpells() {
        return activeSpells.stream().map(SpellTypeId::new).toList();
    }

    /// The spells the player currently has active.
    public PlayerData activeSpells(List<SpellTypeId> activeSpells) {
        this.activeSpells = activeSpells.stream().map(SpellTypeId::index).toList();
        return this;
    }

}
