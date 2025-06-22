package io.github.naomimyselfandi.xanaduwars.core.common;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/// A tag that categorizes actions.
public record ActionTag(@JsonValue @NotNull @Valid Name name) implements Tag {

    /// A tag that categorizes actions.
    public ActionTag(String name) {
        this(new Name(name));
    }

    @Override
    public String toString() {
        return name.toString();
    }

}
