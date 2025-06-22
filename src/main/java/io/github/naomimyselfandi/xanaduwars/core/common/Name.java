package io.github.naomimyselfandi.xanaduwars.core.common;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

/// A declared name.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record Name(@JsonValue @NotNull @Pattern(regexp = "^[A-Z][a-zA-Z0-9]+$") String name) implements Serializable {

    @Override
    public String toString() {
        return name;
    }

}
