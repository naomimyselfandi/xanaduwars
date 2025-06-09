package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

/// A movement table. Movement tables define which units can enter terrain, and
/// how much of their speed they expend when they do so. If more than one of a
/// unit's tags are present in a movement table, the lowest value applies.
public record MovementTable(@Unmodifiable @JsonValue Map<@NotNull @Valid UnitTag, @NotNull @Positive Double> table) {

    /// The empty movement table.
    public static final MovementTable EMPTY = new MovementTable(Map.of());

    /// A movement table. Movement tables define which units can enter terrain,
    /// and how much of their speed they expend when they do so. If more than
    /// one of a unit's tags are present in a movement table, the lowest value
    /// applies.
    /// @implSpec This constructor takes an immutable copy of its argument.
    public MovementTable(Map<UnitTag, Double> table) {
        this.table = Map.copyOf(table);
    }

    /// Look up the movement cost corresponding to a tag set. If none of the
    /// tags are present in this movement table, `NaN` is returned.
    public double cost(Collection<UnitTag> tags) {
        return tags
                .stream()
                .map(table::get)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(Double.NaN);
    }

}
