package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.Pattern;

/// The name of a declaration or unit ability.
public record Name(@JsonValue @Pattern(regexp = "^[A-Z][a-zA-Z0-9]+$") String name) {

    @Override
    public String toString() {
        return name;
    }

}
