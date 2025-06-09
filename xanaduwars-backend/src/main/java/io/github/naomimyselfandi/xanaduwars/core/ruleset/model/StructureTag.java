package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.Pattern;

/// A tag that categorizes structures.
public record StructureTag(@JsonValue @Pattern(regexp = "^[A-Z][a-zA-Z0-9]+$") String label)
        implements Tag, WeaponTarget {

    @Override
    public String toString() {
        return label;
    }

}
