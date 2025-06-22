package io.github.naomimyselfandi.xanaduwars.core.common;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// A tag that categorizes spells.
public record SpellTag(@JsonValue @NotNull @Valid Name name) implements Tag {

    /// A tag that categorizes spells.
    public SpellTag(String name) {
        this(new Name(name));
    }

    @Override
    public String toString() {
        return name.toString();
    }

}
