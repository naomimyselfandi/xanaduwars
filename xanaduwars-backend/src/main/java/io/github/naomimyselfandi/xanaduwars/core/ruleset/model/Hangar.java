package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Set;

/// A hangar definition. This describes what types of unit a unit can carry.
public record Hangar(@Unmodifiable @JsonValue Set<UnitTag> tags) {

    /// A hangar definition that doesn't allow carrying at all.
    public static Hangar EMPTY = new Hangar(Set.of());

    /// A hangar definition. This describes what types of unit a unit can carry.
    /// @implSpec This constructor takes an immutable copy of its argument.
    public Hangar(Set<UnitTag> tags) {
        this.tags = Set.copyOf(tags);
    }

    /// Check if this hangar definition supports any of the given tags.
    public boolean supports(Collection<UnitTag> tags) {
        return this.tags.stream().anyMatch(tags::contains);
    }

}
